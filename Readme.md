To install and run the Apache Flink application please follow the following steps.

- Run the docker compose Yml file and create the cluster
- Verify that the Apache Flink  and Kibana UI work and also elastic search, opening from the browser the following links:
  - <http://localhost:8081>
  - <http://localhost:5601>
  - <http://localhost:9200>
- Save the csv file in the **task manager** container using the following command:
  - docker cp --archive –L C:\Users\nsquarzoni\Documents\dataset.csv docker-taskmanager-1:/opt/flink/dataset.csv

note that the local path will change and possibily the task manager container name might change

- import the kibana dashboard. From the kibana UI home page open the menu on the top left and open the ‘’stack management’’ section.

![](Aspose.Words.6c160b7f-e781-4035-b283-810457f40c54.001.png)![](Aspose.Words.6c160b7f-e781-4035-b283-810457f40c54.002.png)![](Aspose.Words.6c160b7f-e781-4035-b283-810457f40c54.003.png)

Then go to the ‘’saved object’’ and import the ndjson file. You should see the imported dashboard and the imported index.

![](Aspose.Words.6c160b7f-e781-4035-b283-810457f40c54.004.png)![](Aspose.Words.6c160b7f-e781-4035-b283-810457f40c54.005.png)![](Aspose.Words.6c160b7f-e781-4035-b283-810457f40c54.006.png)![](Aspose.Words.6c160b7f-e781-4035-b283-810457f40c54.007.png)

- the easiest way to launch the job is from the Apache Flink UI. Click the ‘’section submit new job’’, add the jar file from your file system and clink on the added line.

![](Aspose.Words.6c160b7f-e781-4035-b283-810457f40c54.008.png)![](Aspose.Words.6c160b7f-e781-4035-b283-810457f40c54.009.png)

Add the entry class and the arguments as follow:

- job.RollsCount
- --input dataset.csv --parall 1 --sleep 500

Note that parall is the parallelism and sleep is the source emission frequency in ms and they can be customized.
