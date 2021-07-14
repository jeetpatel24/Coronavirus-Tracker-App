package io.javabrains.coronavirustracker.services;

import io.javabrains.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class CoronavirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    ArrayList<LocationStats> allStats = new ArrayList<>();

    @PostConstruct  //comment1
    //@Scheduled(cron = "0 10,11,12,13 * *")  //comment2
    @Scheduled(cron = "* * * * * *")
    public void fetchVirusData() throws IOException, InterruptedException {

        ArrayList<LocationStats> newStats = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvBodyReader = new StringReader(response.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        for (CSVRecord record : records) {
            LocationStats locationStats = new LocationStats();
            String state = record.get("Province/State");
            String country = record.get("Country/Region");
            String latestTotalCases = record.get(record.size()-1);

            locationStats.setState(state);
            locationStats.setCountry(country);
            locationStats.setLatestTotalCases(latestTotalCases);

            newStats.add(locationStats);
        }
        this.allStats = newStats;
    }
}



//comment1
//tells spring to execute this method when the application starts


//comment2
/* cron = "<minute> <hour> <day-of-month> <month> <day-of-week>"
    cron = ("0 10,11,12,13 * *") --> 0th minute of 10 to 1 pm of every day of every month of every day of week
    tells spring to run this method on scheduled basis, since the data in the csv file will get updated on daily basis
    this is required because -> suppose we have deployed this application on cloud and server will remain on so data will not get updated using @PostConstruct */