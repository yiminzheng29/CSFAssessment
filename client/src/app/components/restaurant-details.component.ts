import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Comment, Restaurant } from '../models';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-restaurant-details',
  templateUrl: './restaurant-details.component.html',
  styleUrls: ['./restaurant-details.component.css']
})
export class RestaurantDetailsComponent implements OnInit, OnDestroy{
	
	// TODO Task 4 and Task 5
	// For View 3
  params$!:Subscription
  restaurant!: Restaurant
  restaurantId!: string
  commentForm!: FormGroup
  commment!: Comment

  constructor(private restaurantSvc: RestaurantService, private activatedRoute: ActivatedRoute, private fb: FormBuilder, private router: Router) {}

  ngOnInit(): void {
      this.params$ = this.activatedRoute.params.subscribe(
        (params) => {
          this.restaurantId = params['restaurantId']
          console.log("Selected restaurant: ", this.restaurantId)
          this.restaurantSvc.getRestaurant(this.restaurantId)
            .then((result) =>{
              this.restaurant = result
              console.info(">>>> Restaurant details: ", this.restaurant)
              this.commentForm = this.createForm()
            })
            .catch(error => {
              console.error(">>>> error: ", error)
            })
        }
      )
  }

  ngOnDestroy(): void {
      this.params$.unsubscribe()
  }

  processForm() {
    this.commment = this.commentForm.value as Comment
    this.restaurantSvc.postComment(this.commment)
    this.router.navigate(['/'])
  }

  private createForm():FormGroup {
    return this.fb.group({
      restaurantId: this.restaurantId,
      name: this.fb.control<string>('', [Validators.minLength(4)]),
      rating: this.fb.control('',[Validators.min(1), Validators.max(5)]),
      text: this.fb.control<string>('', [Validators.required])
    })
  }

}
