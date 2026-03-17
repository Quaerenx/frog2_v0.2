package com.company.model;

public class HostDTO {
    private String ip;            // IP 주소 (PK)
    private String userName;      // 사용자
    private String purpose;       // 목적
    private String hostLocation;  // 호스트 위치
    private String note;          // 비고
    private String rowColor;      // 행 색상 (hex)

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getHostLocation() { return hostLocation; }
    public void setHostLocation(String hostLocation) { this.hostLocation = hostLocation; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getRowColor() { return rowColor; }
    public void setRowColor(String rowColor) { this.rowColor = rowColor; }
}