package vttp2022.csf.assessment.server.controllers;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Jaas;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.Restaurant;
import vttp2022.csf.assessment.server.repositories.RestaurantRepository;
import vttp2022.csf.assessment.server.services.RestaurantService;

@Controller
@RequestMapping(path="/api")
@CrossOrigin(origins = "*")
public class RestaurantController {
    
    @Autowired
    private RestaurantService restaurantSvc;

    // Task 2 - for storing list of cuisines
    @GetMapping(path="/cuisines")
    @ResponseBody
    public ResponseEntity<List<String>> getCusines() {

        List<String> cuisines = restaurantSvc.getCuisines();
        System.out.println("Cuisines: " + cuisines.toString());

        JsonObjectBuilder jo = Json.createObjectBuilder();
        for (String c: cuisines) {
            jo.add("cuisine", c);
        }
        
        return ResponseEntity.ok(cuisines);

    }   

    @GetMapping(path="/{cuisine}/restaurants")
    @ResponseBody
    public ResponseEntity<String> getRestaurants(@PathVariable String cuisine) {

        List<Restaurant> restaurants = restaurantSvc.getRestaurantsByCuisine(cuisine);

        JsonObjectBuilder jo = Json.createObjectBuilder();
        List<JsonObject> joList = new LinkedList<>();
        for (Restaurant r: restaurants) {
            
            jo.add("restaurantId", r.getRestaurantId());
            jo.add("name", r.getName());
            jo.add("cuisine", r.getCuisine());
            jo.add("address", r.getAddress());
            jo.add("coordinates", restaurantSvc.getValsFromLatLon(r.getCoordinates()));
            jo.add("mapUrl", r.getMapURL());
            joList.add(jo.build());
        }

        System.out.println(joList.toString());

        return ResponseEntity.ok(joList.toString());
    }

    @GetMapping(path="/restaurant/{restaurantId}")
    @ResponseBody
    public ResponseEntity<String> getRestaurant(@PathVariable String restaurantId) {
        
        Optional<Restaurant> opt = restaurantSvc.getRestaurant(restaurantId);

        if (opt.isEmpty()) {
            String error = "Error: RestaurantId: %s not found".formatted(restaurantId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        Restaurant r = opt.get();

        JsonObject jo = Json.createObjectBuilder()
            .add("restaurantId", r.getRestaurantId())
            .add("name", r.getName())
            .add("cuisine", r.getCuisine())
            .add("address", r.getAddress())
            .add("coordinates", restaurantSvc.getValsFromLatLon(r.getCoordinates()))
            .add("mapUrl", r.getMapURL())
            .build();

        return ResponseEntity.ok(jo.toString());
    }

    @PostMapping(path="/comments")
    @ResponseBody
    public ResponseEntity<String> postComment(@RequestBody String payload) {

        System.out.println("Payload >>> " + payload);
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject jo = reader.readObject();

        // convert jo into comment model
        Comment comment = new Comment();
        comment.setName(jo.getString("name"));
        comment.setRestaurantId(jo.getString("restaurantId"));
        comment.setText(jo.getString("text"));
        comment.setRating(jo.getInt("rating"));

        System.out.println(comment.getName());
        restaurantSvc.addComment(comment);

        String response = "Comment posted";

        JsonObject resp = Json.createObjectBuilder()
            .add("message", response)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(resp.toString());

    }

}
