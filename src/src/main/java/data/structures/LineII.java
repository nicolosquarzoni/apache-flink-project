package data.structures;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LineII { //POJO class serializable


    @SerializedName("Id")
    @Expose
    private String id;

    @SerializedName("Mtype")
    @Expose
    private String mtype;

    @SerializedName("Label")
    @Expose
    private String label;

    @SerializedName("Value")
    @Expose
    private float value;

    @SerializedName("Mwidth")
    @Expose
    private int mwidth ;

    @SerializedName("Recipe")
    @Expose
    private int recipe;

    @SerializedName("TS")
    @Expose
    private long ts;


    public float getValue() {
        return value;
    }

    public void setvalue(float value) {
        this.value = value;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMwidth() {
        return mwidth;
    }

    public void setMwidth(int mwidth) {
        this.mwidth = mwidth;
    }

    public int getRecipe() {
        return recipe;
    }

    public void setRecipe(int recipe) {
        this.recipe = recipe;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String toString() {
        return value + "--" + id + "--" + recipe + "--" + ts;
    }

}




