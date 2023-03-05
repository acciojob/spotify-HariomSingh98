package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;//artist-album pair map
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;//list of users
    public List<Song> songs;//list of songs
    public List<Playlist> playlists;//list of playlist
    public List<Album> albums;//list of album
    public List<Artist> artists;//list of artist

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User newUser = new User(name,mobile);
        users.add(newUser);
        return newUser;
    }

    public Artist createArtist(String name) {
        Artist newArtist = new Artist(name);
        artists.add(newArtist);
        return newArtist;
    }

    public Album createAlbum(String title, String artistName) {
        boolean present = false;
        Artist artist =null;
        for(Artist a : artists){//search the artist name in artist list
            if(a.getName().equals(artistName)){
                present=true;
                artist =a;
                break;
            }
        }
        if(present==false)artist = createArtist(artistName);
        Album newAlbum = new Album(title);
        albums.add(newAlbum);

        List<Album> list = new ArrayList<>();

        if(artistAlbumMap.containsKey(artist))list = artistAlbumMap.get(artist);
        list.add(newAlbum);

        artistAlbumMap.put(artist,list);

        return newAlbum;

    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean present = false;
        Album album = null;
        for(Album al : albums){
            if(al.getTitle().equals(albumName)){
                present=true;
                album=al;
                break;
            }
        }
        if(present==false)throw new Exception("Album does not exist");


        Song newSong = new Song(title,length);
        songs.add(newSong);

        List<Song> songList = new ArrayList<>();
        if(albumSongMap.containsKey(album))songList= albumSongMap.get(album);
        songList.add(newSong);

        albumSongMap.put(album,songList);

        return newSong;

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
         boolean present = false;
         User user = null;
         for(User u : users){
             if(u.getMobile().equals(mobile)){
                 present=true;
                 user= u;
                 break;
             }
         }
         if(present==false)throw new Exception( "User does not exist");

         Playlist newPlayList = new Playlist(title);

         playlists.add(newPlayList);

         List<Song> desiredSong = new ArrayList<>();//list of songs
         for(Song s : songs){
             if(s.getLength()==length){
                 desiredSong.add(s);
             }
         }

        playlistSongMap.put(newPlayList,desiredSong);

         creatorPlaylistMap.put(user,newPlayList);
         List<User> listener = new ArrayList<>();
         listener.add(user);

         playlistListenerMap.put(newPlayList,listener);


        if(userPlaylistMap.containsKey(user)){
            List<Playlist> userPlayList = userPlaylistMap.get(user);
            userPlayList.add(newPlayList);
            userPlaylistMap.put(user,userPlayList);
        }else{
            List<Playlist> plays = new ArrayList<>();
            plays.add(newPlayList);
            userPlaylistMap.put(user,plays);
        }

        return newPlayList;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        boolean present = false;
        User user = null;
        for(User u : users){
            if(u.getMobile().equals(mobile)){
                present=true;
                user= u;
                break;
            }
        }
        if(present==false)throw new Exception( "User does not exist");

        Playlist newPlayList = new Playlist(title);

        List<Song> desiredSong = new ArrayList<>();//list of songs
        for(Song s : songs){
            if(songTitles.contains(s.getTitle())){
                desiredSong.add(s);
                break;
            }
        }

        playlistSongMap.put(newPlayList,desiredSong);


        creatorPlaylistMap.put(user,newPlayList);

        List<User> listener = new ArrayList<>();
        listener.add(user);
        playlistListenerMap.put(newPlayList,listener);


        if(userPlaylistMap.containsKey(user)){
            List<Playlist> userPlayList = userPlaylistMap.get(user);
            userPlayList.add(newPlayList);
            userPlaylistMap.put(user,userPlayList);
        }else{
            List<Playlist> plays = new ArrayList<>();
            plays.add(newPlayList);
            userPlaylistMap.put(user,plays);
        }


        return newPlayList;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        boolean isPlaylistPresent = false;
        Playlist pl = null;
        for(Playlist p : playlists){
            if(p.getTitle().equals(playlistTitle)){
                isPlaylistPresent=true;
                pl = p;
                break;
            }
        }
        if(isPlaylistPresent==false)throw new Exception("Playlist does not exist");
        boolean isUserPrsent = false;
        User u = null;
        for(User us : users){
            if(us.getMobile().equals(mobile)){
                isUserPrsent=true;
                u=us;
                break;
            }
        }
        if(isUserPrsent==false)throw new Exception("User does not exist");

        if(creatorPlaylistMap.containsKey(u))
            return pl;

        List<User> listener = playlistListenerMap.get(pl);
        for(User user:listener){
            if(user.equals(u))
                return pl;
        }

        listener.add(u);

        playlistListenerMap.put(pl,listener);

        List<Playlist> playlistList = new ArrayList<>();

        if(userPlaylistMap.containsKey(u))playlistList = userPlaylistMap.get(u);

        playlistList.add(pl);

        userPlaylistMap.put(u,playlistList);


        return pl;


    }


    public Song likeSong(String mobile, String songTitle) throws Exception {
         boolean isUserPresent= false;
         User user = null;
         for(User u : users){
             if(u.getMobile().equals(mobile)){
                 isUserPresent=true;
                 user= u;
                 break;
             }
         }
         if(isUserPresent==false)throw new Exception("User does not exist");
        boolean isSongPresent= false;
        Song song = null;
        for(Song s : songs){
            if(s.getTitle().equals(songTitle)){
                isSongPresent=true;
                song= s;
                break;
            }
        }
        if(isSongPresent==false)throw new Exception("Song does not exist");



        if(songLikeMap.containsKey(song)){//check if song is present in map
            List<User> userlist = songLikeMap.get(song);
            if(userlist.contains(user)){//if it is already liked by the user
                return song;
            }
            else {//if it is not liked by user yet
                int likes = song.getLikes() + 1;
                song.setLikes(likes);
                userlist.add(user);
                songLikeMap.put(song,userlist);

                Album album=null;
                for(Album album1:albumSongMap.keySet()){
                    List<Song> songList = albumSongMap.get(album1);
                    if(songList.contains(song)){
                        album = album1;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist artist1:artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(artist1);
                    if (albumList.contains(album)){
                        artist = artist1;
                        break;
                    }
                }
                int likes1 = artist.getLikes() +1;
                artist.setLikes(likes1);
                artists.add(artist);
                return song;
            }
        }
        else {//song is not present in like map yet
            int likes = song.getLikes() + 1;
            song.setLikes(likes);
            List<User> newlist = new ArrayList<>();
            newlist.add(user);
            songLikeMap.put(song,newlist);

            Album album=null;
            for(Album album1:albumSongMap.keySet()){
                List<Song> songList = albumSongMap.get(album1);
                if(songList.contains(song)){
                    album = album1;
                    break;
                }
            }
            Artist artist = null;
            for(Artist artist1:artistAlbumMap.keySet()){
                List<Album> albumList = artistAlbumMap.get(artist1);
                if (albumList.contains(album)){
                    artist = artist1;
                    break;
                }
            }
            int likes1 = artist.getLikes() +1;
            artist.setLikes(likes1);
            artists.add(artist);

            return song;
        }
    }

    public String mostPopularArtist() {
        int max = 0;
        Artist artist=null;

        for(Artist a:artists){
            if(a.getLikes()>=max){
                artist=a;
                max = a.getLikes();
            }
        }
        if(artist==null)
            return null;
        else
            return artist.getName();
    }

    public String mostPopularSong() {
        int max=0;
        Song song = null;

        for(Song s: songLikeMap.keySet()){
            if(s.getLikes()>=max){
                song=s;
                max = s.getLikes();
            }
        }
        if(song==null)
            return null;
        else
            return song.getTitle();
    }
}
