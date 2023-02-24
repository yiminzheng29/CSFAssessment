import { NgModule } from "@angular/core";
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from "@angular/common/http";
import { AppComponent } from './app.component';
import { CuisineListComponent } from './components/cuisine-list.component';
import { RestaurantCuisineComponent } from './components/restaurant-cuisine.component';
import { RestaurantDetailsComponent } from './components/restaurant-details.component';
import { RestaurantService } from "./restaurant-service";
import { ReactiveFormsModule } from "@angular/forms";
import { RouterModule, Routes } from "@angular/router";

const routes: Routes = [
  {path: '', component: CuisineListComponent},
  {path: 'restaurants/:cuisine', component: RestaurantCuisineComponent},
  {path: 'restaurant/:restaurantId', component: RestaurantDetailsComponent},
  {path: '**', redirectTo: '/', pathMatch: 'full'}
]
@NgModule({
  declarations: [
    AppComponent,
    CuisineListComponent,
    RestaurantCuisineComponent,
    RestaurantDetailsComponent
  ],

  imports: [
    BrowserModule, HttpClientModule, ReactiveFormsModule,
    RouterModule.forRoot(routes, {useHash: true})
  ],

  providers: [RestaurantService],
  bootstrap: [AppComponent]
})
export class AppModule { }
