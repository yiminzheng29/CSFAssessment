import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Restaurant } from '../models';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-restaurant-cuisine',
  templateUrl: './restaurant-cuisine.component.html',
  styleUrls: ['./restaurant-cuisine.component.css']
})
export class RestaurantCuisineComponent implements OnInit, OnDestroy{
	
	// TODO Task 3
	// For View 2
  restaurants: Restaurant[] = []
  query!: string
  params$!: Subscription

  constructor(private restaurantSvc: RestaurantService, private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
      this.params$ = this.activatedRoute.params.subscribe(
        (params) => {
          this.query = params['cuisine']
          console.info(">>>> Cuisine selected: ", this.query)
          this.restaurantSvc.getRestaurantsByCuisine(this.query)
            .then((results) => {
              this.restaurants = results
              console.info(">>>> restaurants available: ", this.restaurants)
            }).catch(error => {
              console.error(">>>> error: ", error)
            })
        }

      )
  }

  ngOnDestroy(): void {
      this.params$.unsubscribe()
  }
}
