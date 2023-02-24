package vttp2022.csf.assessment.server.repositories;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.LatLng;
import vttp2022.csf.assessment.server.models.Restaurant;

@Repository
public class RestaurantRepository {

	public static final String C_RESTAURANTS = "restaurants";
	public static final String C_COMMENTS = "comments";

	@Autowired
	private MongoTemplate template;

	
	// TODO Task 2
	// Use this method to retrive a list of cuisines from the restaurant collection
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	// Write the Mongo native query above for this method
	/**
	 * db.restaurant.distinct("cuisine")
	 * 
	 */
	//  
	public List<String> getCuisines() {
		// Implmementation in here
		List<String> cuisines = new LinkedList<>();
		cuisines = template.findDistinct( new Query(), "cuisine", C_RESTAURANTS, String.class);
		cuisines = cuisines.stream().map(s -> s.replaceAll("/", "_")).collect(Collectors.toList());

		return cuisines;
	}

	// TODO Task 3
	// Use this method to retrive a all restaurants for a particular cuisine
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	// Write the Mongo native query above for this method
	// 
	/**
	 * db.restaurants.aggregate([
//  {
//   $project: {
//    _id: 0,
//    name: 1,
//    restaurant_id: 1,
//    cuisine: 1,
//    address: {
//     $concat: [ "$address.building", ", ", "$address.street", ", ", "$address.zipcode", ", ", "$borough" ]
//    },
//    coordinates: "$address.coord"
//   }
//  }.sort({name: 1})
// ]);

	 */
	//  
	public List<Restaurant> getRestaurantsByCuisine(String cuisine) {
		// Implmementation in here

		// change _ to / back in query
		cuisine = cuisine.replaceAll("_", "/");

		Criteria criteria = Criteria.where("cuisine").is(cuisine);
		Query query = Query.query(criteria);
		query.with(Sort.by(Sort.Direction.ASC, "name"));

		List<Restaurant> restaurants = new LinkedList<>();
		
		List<Document> resDocs = template.find(query, Document.class, C_RESTAURANTS);

		for (Document d: resDocs) {
			restaurants.add(create(d));
		}

		return restaurants;

	}

	public Restaurant create(Document d) {
		Restaurant r = new Restaurant();
		r.setName(d.getString("name"));
		r.setRestaurantId(d.getString("restaurant_id"));
		r.setAddress("%s, %s, %s, %s".formatted(d.get("address", Document.class).getString("building"), d.get("address", Document.class).getString("street"), d.get("address", Document.class).getString("zipcode"), d.get("address", Document.class).getString("borough")));

		LatLng latLng = new LatLng();

		Float lon = Float.parseFloat(d.get("address", Document.class).get("coord", ArrayList.class).get(0).toString());
		Float lat = Float.parseFloat(d.get("address", Document.class).get("coord", ArrayList.class).get(1).toString());

		latLng.setLatitude(lat);
		latLng.setLongitude(lon);

		r.setCoordinates(latLng);
		r.setCuisine(d.getString("cuisine"));
		r.setMapURL("");

		return r;

	}


	// TODO Task 4
	// Use this method to find a specific restaurant
	// You can add any parameters (if any) 
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	// Write the Mongo native query above for this method

	// db.restaurants.find({restaurant_id: "restaurantId"})
	//  
	public Optional<Restaurant> getRestaurant(String restaurantId) {
		// Implmementation in here
		Criteria criteria = Criteria.where("restaurant_id").is(restaurantId);

		Query query = Query.query(criteria);

		Document result = template.findOne(query, Document.class, C_RESTAURANTS);

		if (result == null)
			return Optional.empty();

		return Optional.of(create(result));
		
	}

	// TODO Task 5
	// Use this method to insert a comment into the restaurant database
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	// Write the Mongo native query above for this method
	/**
	 * 
	 * @param comment
	 * db.comment.insert({
	 * restaurant_id: "40827287",
	 * name: "Fred",
	 * rating: 2,
	 * text: "Not good"})
	 */
	//  
	public void addComment(Comment comment) {
		// Implmementation in here
		Document doc = new Document();
		doc.put("restaurant_id", comment.getRestaurantId());
		doc.put("name", comment.getName());
		doc.put("rating", comment.getRating());
		doc.put("text", comment.getText());

		System.out.println("Comments inserted: "+ comment.getName());

		template.insert(doc, C_COMMENTS);
	}
	
	// You may add other methods to this class

}
