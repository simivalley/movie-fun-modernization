/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.superbiz.moviefun.moviesapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class MoviesClient {



    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String movieServiceUrl;
    private RestTemplate restTemplate;

    public MoviesClient(String movieServiceUrl, RestTemplate restTemplate) {
        this.movieServiceUrl = movieServiceUrl;
        this.restTemplate = restTemplate;
    }

    public MovieInfo find(Long id) {
        return restTemplate.getForObject(movieServiceUrl + "/" + id, MovieInfo.class);
    }

    public void addMovie(MovieInfo movie) {
        logger.debug("Creating movie with title {}, and year {}", movie.getTitle(), movie.getYear());

        restTemplate.put(movieServiceUrl, movie);


    }


    public void updateMovie(MovieInfo movie) {
        restTemplate.put(movieServiceUrl, movie);

    }


    public void deleteMovie(MovieInfo movie) {
        restTemplate.delete(movieServiceUrl, movie);
    }

    public void deleteMovieId(long id) {
        MovieInfo movie = find(id);
        deleteMovie(movie);
    }

    public List<MovieInfo> getMovies() {
        ResponseEntity<List<MovieInfo>> moviesResponse =
                restTemplate.exchange(movieServiceUrl,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<MovieInfo>>() {
                        });
        List<MovieInfo> movies = moviesResponse.getBody();
        return movies;
    }

    public List<MovieInfo> findAll(int firstResult, int maxResults) {
        List<MovieInfo> movies = getMovies();

        return movies.subList(firstResult, maxResults);
    }

    public int countAll() {

        return getMovies().size();
    }

    public int count(String field, String searchTerm) {

        List<MovieInfo> movies = getMovies();

        switch (field.trim().toLowerCase()) {
            case "director":
                long countDirectors = movies
                        .stream()
                        .filter(m -> m.getDirector().equalsIgnoreCase(searchTerm))
                        .count();
                return (int) countDirectors;
            case "title":
                long countTitles = movies
                        .stream()
                        .filter(m -> m.getTitle().equalsIgnoreCase(searchTerm))
                        .count();
                return (int) countTitles;
            case "genre":
                long countGenre = movies
                        .stream()
                        .filter(m -> m.getGenre().equalsIgnoreCase(searchTerm))
                        .count();
                return (int) countGenre;


            default:
                return 0;
        }


    }

    public List<MovieInfo> findRange(String field, String searchTerm, int firstResult, int maxResults) {

        List<MovieInfo> movies = getMovies();
        List<MovieInfo> filteredMovies = new LinkedList<>();
        switch (field.trim().toLowerCase()) {

            case "director":
                return movies.stream().filter(m -> m.getDirector().equalsIgnoreCase(searchTerm))
                        .collect(Collectors.toList()).subList(firstResult,maxResults);

            case "title":
                return movies.stream().filter(m -> m.getTitle().equalsIgnoreCase(searchTerm))
                        .collect(Collectors.toList()).subList(firstResult,maxResults);
            case "genre":
                return movies.stream().filter(m -> m.getGenre().equalsIgnoreCase(searchTerm))
                        .collect(Collectors.toList()).subList(firstResult,maxResults);
            default:
                return filteredMovies;
        }

    }

    public void clean() {
        List<MovieInfo> movieInfos = getMovies();

        for ( MovieInfo m : movieInfos){
            deleteMovie(m);

        }
    }
}
