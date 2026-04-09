package ug.daes.OnBoardingTransactionHandler.dto;

public class MethodNature
{
    private String methodName;
    private  String nature;
    private String api;
    private String className;
    private int par;
    private String args;
    private boolean auth;
    private String activity;


    public MethodNature() { }

    public MethodNature(String methodName, String nature, String api, String className, int par, String args, boolean auth,String activity) {
        this.methodName = methodName;
        this.nature = nature;
        this.api = api;
        this.className = className;
        this.par = par;
        this.args = args;
        this.auth = auth;
        this.activity=activity;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
