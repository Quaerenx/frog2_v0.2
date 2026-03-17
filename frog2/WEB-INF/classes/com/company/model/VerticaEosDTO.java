package com.company.model;

import java.util.Date;

public class VerticaEosDTO {
    private String version;
    private Date releaseDate;
    private Date endOfServiceDate;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getEndOfServiceDate() {
        return endOfServiceDate;
    }

    public void setEndOfServiceDate(Date endOfServiceDate) {
        this.endOfServiceDate = endOfServiceDate;
    }
}


