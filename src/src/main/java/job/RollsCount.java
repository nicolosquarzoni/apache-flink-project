package job;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import data.structures.LineII;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.elasticsearch.sink.Elasticsearch7SinkBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.http.HttpHost;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class RollsCount {

    public static HashMap<String, Long> memo = new HashMap<String, Long>(); //to memorize timestamps in processdatastream3

    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args); //input parameters from CLI
        String input = params.get("input");
        String parall = params.get("parall");
        String sleep = params.get("sleep");

        //1. creates a stream execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        ProvaDataSource source = new ProvaDataSource(); // initialize data source class
        source.setIn(input);
        source.setSleep(Integer.parseInt(sleep));
        //2.creates a data stream from a source
        DataStream<String> inputdatastream = env.addSource(source);

        //3. processes the data stream
        DataStream<LineII> processdatastream1 = inputdatastream.map(new MapFunction<String, LineII>() {
            @Override
            public LineII map(String s) throws Exception {
                return new Gson().fromJson(s, LineII.class); //POJO parsing
            }
        });

        DataStream<LineII> processdatastream2 = processdatastream1.filter(new FilterFunction<LineII>() {
            @Override
            public boolean filter(LineII line) throws Exception {
                return line.getMtype().contains("LS");
            }
        });

        DataStream<LineII> processdatastream3 = processdatastream2.map(new MapFunction<LineII, LineII>() {
            @Override
            public LineII map(LineII line) throws Exception {
                line.setLabel("Rolls produced");
                if (!memo.containsKey(line.getId())){ //job start handling
                    memo.put(line.getId(), line.getTs());
                    line.setvalue(0);
                }
                else{
                    float t4logs = (line.getMwidth()/(line.getRecipe()*line.getValue()))*60; //in s
                    long deltat = (line.getTs() - memo.get(line.getId()))/1000; //in s
                    line.setvalue(4*(deltat/t4logs)*((float)line.getMwidth()/ line.getRecipe())); //rolls count formula
                    memo.put(line.getId(), line.getTs());
                }
                return line;
            }
        });

        //4. Sink the data stream to Elasticsearch
        processdatastream3.sinkTo(
                new Elasticsearch7SinkBuilder<LineII>()
                        .setBulkFlushMaxActions(1) // Instructs the sink to emit after every element, otherwise they would be buffered
                        .setHosts(new HttpHost("elasticsearch", 9200, "http"))
                        .setEmitter(
                                (element, context, indexer) ->
                                        indexer.add(createIndexRequest(element)))
                        .build()).setParallelism(Integer.parseInt(parall));



        //5 esegui
        env.execute(RollsCount.class.getName());
    }

    private static IndexRequest createIndexRequest(LineII element) { //sets data to send to elastc search from the data received
        Map<String, Object> json = new HashMap<>();
        json.put("timestamp", new Date());
        json.put("Roll produced", (int)element.getValue());
        json.put("Line ID", element.getId());
        return Requests.indexRequest()
                .index("myindex9")
                .source(json);
    }




    public static class ProvaDataSource implements SourceFunction<String> { //interface implementation
        Random random = new Random();
        String in = new String(); //for the CSV file folder path from CLI input
        Integer sleep; //for data stream frequency from CLI input
        @Override
        public void run(SourceContext<String> ctx) throws Exception {
            FileReader filereader = new FileReader(in);
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) { // reading data line by line
                JSONObject object = new JSONObject(); //converting the CSV file line into a JSON object
                object.put("Id", nextRecord[0] );
                object.put("Mtype", nextRecord[1]);
                object.put("Label", nextRecord[3]);
                object.put("Value", nextRecord[4]);
                object.put("Mwidth", 2800);
                object.put("Recipe", 110);
                object.put("TS", System. currentTimeMillis());

                ctx.collect(object.toString()); //Flink method to create the stream

                Thread.sleep(sleep); //Sets the emission frequancy of the read line of CSV file
            }

        }

        public void setIn(String in){
            this.in = in;
        }

        public void setSleep(Integer sleep){
            this.sleep = sleep;
        }

        @Override
        public void cancel() {

        }
    }
}

