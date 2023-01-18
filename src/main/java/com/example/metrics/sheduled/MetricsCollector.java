package com.example.metrics.sheduled;

import com.example.metrics.entity.Metrics;
import com.example.metrics.repository.MetricsRepository;
import io.quickchart.QuickChart;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetricsCollector {


    @Autowired
    MetricsRepository metricsRepository;

    @Scheduled(fixedRate = 50000)
    public void pollConsistently() {

        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromHttpUrl("https://yandex.ru/search/xml")
                .queryParam("user", "leonis9")
                .queryParam("key", "03.157486687:7cd8129b6dbbb1767096b8ae20038152")
                .queryParam("query", "who is pepega???")
                .build().toUri();

        StopWatch stopwatch = StopWatch.createStarted();

        ResponseEntity<String> res = restTemplate.getForEntity(uri, String.class);

        System.out.println(res.getBody());

        stopwatch.stop();

        Metrics metric = new Metrics();
        metric.setMsRequestPending(stopwatch.getNanoTime());
        metric.setMsDateTime(new Date());

        metricsRepository.save(metric);
    }

    @Scheduled(fixedRate = 500000, initialDelay = 1000)
    public void drawAndUploadChart() throws IOException {
        QuickChart chart = new QuickChart();
        chart.setWidth(500);
        chart.setHeight(300);

        List<Metrics> metrics = metricsRepository.findAll();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");

        String config = "{"
                + "    type: 'bar',"
                + "    data: {"
                + "        labels: [" + metrics.stream().map(x -> "\'" + dateFormat.format(x.getMsDateTime()) + "\'").collect(Collectors.joining(",")) + "],"
                + "        datasets: [{"
                + "            label: 'Requests_elapsed_time',"
                + "            data: [" + metrics.stream().map(x -> x.getMsRequestPending().toString()).collect(Collectors.joining(",")) + "]"
                + "        }]"
                + "    },"
                + "    options: {"
                + "        scales: {"
                + "            xAxes: ["
                + "                 {"
                + "                    scaleLabel: {"
                + "                         display: true,"
                + "                         fontColor: '#000000',"
                + "                         fontSize: 20,"
                + "                         labelString: 'Elapsed_time',"
                + "                    },"
                + "                 },"
                + "             ],"
                + "             yAxes: ["
                + "                 {"
                + "                     scaleLabel: {"
                + "                         display: true,"
                + "                         labelString: 'Request_time,nanoSec',"
                + "                         fontColor: '#000000',"
                + "                         fontSize: 20,"
                + "                     },"
                + "                 },"
                + "             ]"
                + "         }"
                + "     }"
                + "}";

        config = config.replace(" ", "");

        chart.setConfig(config);

        String chartUrl = chart.getUrl();
        System.out.println(chartUrl);

        RestTemplate restTemplate = new RestTemplate();

        URL url = new URL(chartUrl);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1!=(n=in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] imageBytes = out.toByteArray();

        Path picture = Files.write(Paths.get("./image.webp"), imageBytes);


        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.imgbb.com/1/upload")
                .queryParam("key", "10511e5d2ef3a1e0377d01576049fe9f")
                .build()
                .toUri();

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource(picture));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);


        System.out.println(uri.toString());

        ResponseEntity<String> res = restTemplate.postForEntity(uri, requestEntity, String.class);
        System.out.println(res.getBody());
    }

    @Scheduled(fixedRate = 500000, initialDelay = 2000)
    public void recognizeTextFromImage() {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.ocr.space/parse/imageurl")
                .queryParam("apikey", "K89541776288957")
                .queryParam("url", "https://cdn.trinixy.ru/uploads/posts/2017-09/1506092031_kartinki_s_nadpisiami_22.jpg")
                .build().toUri();

        System.out.println("Send");

        ResponseEntity<HashMap> res = restTemplate.getForEntity(uri, HashMap.class);
        System.out.println(res.getBody().get("ParsedResults"));
    }
}