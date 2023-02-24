// Do not change these interfaces
export interface Restaurant {
	restaurantId: string
	name: string
	cuisine: string
	address: string
	coordinates: number[]
	mapUrl: string
}

export interface Comment {
	name: string
	rating: number
	restaurantId: string
	text: string
}
