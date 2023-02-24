package vttp2022.csf.assessment.server.repositories;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import vttp2022.csf.assessment.server.models.Restaurant;

@Repository
public class MapCache {

	private static final String URL = "http://map.chuklee.com/map";

	@Autowired
	private AmazonS3 s3;

	@Value("${spaces.bucket}")
	private String spacesBucket;

	@Value("${spaces.endpoint.url}")
	private String spacesEndpointUrl;


	// TODO Task 4
	// Use this method to retrieve the map
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME

	// searches for http://map.chuklee.com/map?lat=40.6580753&lng=-73.9829239
	public String getMap(Restaurant r) {
		// Implmementation in here

		// if link already saved in s3:
		if (r.getMapURL()!="") {
			return r.getMapURL();
		}

		// if link not yet saved:
		else {
			byte[] payload;
			String url = UriComponentsBuilder.fromUriString(URL)
				.queryParam("lat", r.getCoordinates().getLatitude())
				.queryParam("lng", r.getCoordinates().getLongitude())
				.toUriString();

			System.out.println(url);

			RequestEntity<Void> req = RequestEntity.get(url).build();

			RestTemplate template = new RestTemplate();
			ResponseEntity<byte[]> resp;

			resp = template.exchange(req, byte[].class);

			payload = resp.getBody();

			System.out.println(payload.toString());

			File file = new File(r.getRestaurantId());

			try {

				OutputStream os = new FileOutputStream(file);

				os.write(payload);
				System.out.println("Byte inserted");

				// ObjectMetadata metaData = new ObjectMetadata();
				// metaData.setContentType("image/jpeg");

				PutObjectRequest putReq = new PutObjectRequest(spacesBucket, r.getRestaurantId(), file);
				s3.putObject(putReq);

				os.flush();
				os.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}

			String mapUrl = "https://%s.%s/%s".formatted(spacesBucket, spacesEndpointUrl, r.getRestaurantId());
			System.out.println(mapUrl);
			r.setMapURL(mapUrl);

		return mapUrl;
	}

	}


	// You may add other methods to this class

		}
