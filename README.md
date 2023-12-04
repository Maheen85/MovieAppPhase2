
# Movie App Phase 2

Display movies from MovieDB API and allow user to save and sort movies list.

This is the second phase of the Movie App project, an Android application that allows users to discover and explore movies. The project involves fetching movie data from The Movie Database (TMDb) API, displaying details about each movie, managing favorites, and more.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
  - [Model-View-ViewModel (MVVM)](#model-view-viewmodel-mvvm)
  - [Room Database](#room-database)
- [Key Components](#key-components)
  - [Adapters](#adapters)
  - [Database](#database)
  - [Remote](#remote)
  - [Repository](#repository)
- [UI Screens](#ui-screens)
- [Usage](#usage)
- [License](#license)

## Overview

The Movie App Phase 2 builds upon the features introduced in Phase 1. It incorporates additional functionalities such as movie reviews, trailers, and a favorite system. The app is designed to provide an intuitive and engaging experience for users interested in discovering information about their favorite movies.

## Features

- Display top-rated and popular movies
- View detailed information about each movie
- Watch trailers and read reviews
- Mark movies as favorites
- Persistence of favorite movies using Room Database
  

## Architecture

### Model-View-ViewModel (MVVM)

The project follows the MVVM architectural pattern to separate concerns and enhance maintainability. Key components include:

- **Model**: Represents the data and business logic, including the Room Database for favorites.
- **View**: Displays UI elements and observes ViewModel changes.
- **ViewModel**: Acts as a mediator between the Model and View, holding and processing UI-related data.

### Room Database

Room is used for local data storage, particularly for managing favorite movies. The AppDatabase, FavoriteDao, and FavoriteEntry are integral components for this functionality.

## Key Components

### Adapters

#### MoviesAdapter

Responsible for adapting movie data for display in RecyclerViews. Handles the presentation of movie items and responds to user clicks.

#### ReviewAdapter

Adapts review data for display in a RecyclerView, featuring expandable TextViews for lengthy reviews.

#### TrailerAdapter

Adapts trailer data for display in a RecyclerView, allowing users to watch trailers on YouTube.

### Database

#### AppDatabase

A Room Database class that holds the SQLite database for storing favorite movies.

#### FavoriteDao

Data Access Object (DAO) interface defining database operations related to favorite movies.

#### FavoriteEntry

Entity class representing a favorite movie, with Room annotations for database mapping.

### Remote

#### Client

Retrofit client setup for making network requests to TMDb API.

#### MovieApiService

Retrofit service interface defining API endpoints for fetching movie data.

### Repository

#### FavoriteRepository

Manages data operations, serving as a clean API to the rest of the application. Integrates with ViewModel and data sources.

## UI Screens

- **MainActivity**: Displays a grid of top-rated and popular movies.
- **DetailActivity**: Shows detailed information, reviews, and trailers for a selected movie.

  
<p align="center">
<img src="https://user-images.githubusercontent.com/32513021/193514149-e9752971-061c-4e17-9278-8f98bdfaecf0.jpg" width="200" height="350" />
</p>


<p align="center">
<img src="https://user-images.githubusercontent.com/32513021/193514174-12bcc912-56dd-40e8-a685-aab761ce5b97.jpg" width="200" height="350" />
</p>



<p align="center">
<img src="https://user-images.githubusercontent.com/32513021/193514208-60963aaf-57f3-49e1-8f6a-e3355f669b6c.jpg" width="200" height="350" />
</p>


<p align="center">
<img src="https://user-images.githubusercontent.com/32513021/193514258-41321e51-51b5-4857-bb17-987c93f4401f.jpg" width="200" height="350" />
</p>


## Usage

To run the app locally, follow these steps:

1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the app on an emulator or physical device.


## License

This project is licensed under the [MIT License](LICENSE).

