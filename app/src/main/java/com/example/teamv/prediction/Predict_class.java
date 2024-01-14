package com.example.teamv.prediction;


public class Predict_class {
    private String age;
    private String bmi;
    private  String timework;
    private String env;
    private String path;
    private String heal;
    private String sex;
    private String nature;
    public Predict_class(){
    }
    public Predict_class(int age,int timework, float bmi,  int env, int path, int heal, int sex, int nature) {
        this.age = "Tuổi: "+ String.valueOf(age);
        this.bmi = "BMI: "+ String.valueOf(bmi);
        this.timework = "Thời gian làm việc trên tuần: "+String.valueOf(timework);
        if(env==0){
            this.env="Môi truường làm việc: An toàn";
        } else if (env==1) {
            this.env="Môi truường làm việc: Bình thường";
        }else if (env==2) {
            this.env="Môi truường làm việc: Khá nguy hiểm";
        }else if (env==3) {
            this.env="Môi truường làm việc: Nguy hiểm";
        }
        if(path==0){
            this.path="Bệnh lý: Không có";
        } else if (path==1) {
            this.path="Bệnh lý: Bệnh lí cấp tính không kiểm soát";
        }else if (path==2) {
            this.path="Bệnh lý: Nặng";
        }

        if(heal==0){
            this.heal="Môi truường làm việc: An toàn";
        } else if (heal==1) {
            this.heal="Môi truường làm việc: Bình thường";
        }else if (heal==2) {
            this.heal="Môi truường làm việc: Khá nguy hiểm";
        }else if (heal==3) {
            this.heal="Môi truường làm việc: Nguy hiểm";
        }
        if(sex==0){
            this.sex="giới tính: nữ";
        }else{
            this.sex ="giới tính: nam";
        }
        if(nature==0){
            this.nature="Tính chất công việc: Nhẹ nhàng";
        } else if (nature==1) {
            this.nature="Tính chất công việc: Bình thường";
        }else if (nature==2) {
            this.nature="Tính chất công việc: Nặng nhọc";
        }
    }

    public String getAge() {
        return age;
    }

    public String getBmi() {
        return bmi;
    }

    public String getTimework() {
        return timework;
    }

    public String getEnv() {
        return env;
    }

    public String getPath() {
        return path;
    }

    public String getHeal() {
        return heal;
    }

    public String getSex() {
        return sex;
    }

    public String getNature() {
        return nature;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public void setTimework(String timework) {
        this.timework = timework;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setHeal(String heal) {
        this.heal = heal;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }
}
