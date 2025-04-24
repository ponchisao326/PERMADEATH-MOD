package com.victorgponce.permadeath_mod.config;

public class Config {
    private String jdbc;
    private String user;
    private String password;
    private int day;
    private boolean deathTrain;

    // Getters y setters...
    public String getJdbc() { return jdbc; }
    public void setJdbc(String jdbc) { this.jdbc = jdbc; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }
    public boolean isDeathTrain() { return deathTrain; }
    public void setDeathTrain(boolean deathTrain) { this.deathTrain = deathTrain; }

}
