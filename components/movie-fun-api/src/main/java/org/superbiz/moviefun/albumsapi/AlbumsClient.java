package org.superbiz.moviefun.albumsapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class AlbumsClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String albumServiceUrl;
    private RestTemplate restTemplate;

    public AlbumsClient(String albumServiceUrl, RestTemplate restTemplate) {
        this.albumServiceUrl = albumServiceUrl;
        this.restTemplate = restTemplate;
    }

    public void addAlbum(AlbumInfo album) {
        System.out.println (  " addAlbum *******************************************");
        System.out.println (  " albumServiceURL ******************************* :"  + albumServiceUrl);
        logger.debug("Creating album with artist {}, and title {}", album.getArtist(), album.getTitle());
        restTemplate.postForEntity(albumServiceUrl, album, AlbumInfo.class);
    }

    public AlbumInfo find(long id) {
        return restTemplate.getForObject(albumServiceUrl + "/" + id, AlbumInfo.class);
    }

    public List<AlbumInfo> getAlbums() {
        ResponseEntity<List<AlbumInfo>> albumsResponse =
                restTemplate.exchange(albumServiceUrl,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumInfo>>() {
                        });
        return albumsResponse.getBody();
    }

    public void deleteAlbum(AlbumInfo album) {
        restTemplate.delete(albumServiceUrl + "/" + album.getId());
    }

    public void updateAlbum(AlbumInfo album) {
        restTemplate.put(albumServiceUrl, album);
    }
}
