/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectmusic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 *
 * @author huudu
 */
public class MusicServer {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Song";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "123";
    
//    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server đã chạy, đợi kết nối.......");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Đã kết nối với: " + clientSocket.getInetAddress());

                try (
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
                ) {
//                    String searchQuery = (String) in.readObject();
//                    List<Song> searchResults = searchSongs(searchQuery);
//
//                    out.writeObject(searchResults);
                    String searchType = ((String) in.readObject()).toLowerCase(); // Đọc loại tìm kiếm

                    if ("nhạc".equals(searchType)) {
                        String searchQuery = (String) in.readObject();
                        List<Song> searchResults = searchSongs(searchQuery);
                        out.writeObject(searchResults);
                    } else if ("ca sĩ".equals(searchType)) {
                        String searchQuery = (String) in.readObject();
                        List<Artist> searchResults = searchArtists(searchQuery);
                        out.writeObject(searchResults);
                    } else if ("nhạc của ca sĩ".equals(searchType)) {
                        String searchQuery = (String) in.readObject();
                        List<Song> searchResults = getSongsByArtist(searchQuery);
                        out.writeObject(searchResults);                  
                    } else {
                        // Xử lý loại tìm kiếm không hợp lệ (tuỳ thuộc vào yêu cầu của bạn)
                        System.out.println("Loại tìm kiếm không hợp lệ");
                    }

                    
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    clientSocket.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    private static List<Song> searchSongs(String searchQuery) {
        List<Song> searchResults  = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sqlQuery = "SELECT * FROM Songs WHERE Title LIKE ?";
            try (                  
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, "%" + searchQuery + "%");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String songName = resultSet.getString("Title");
                        String artist = resultSet.getString("Artist");
                        String lyrics = resultSet.getString("Lyrics");
                        String youtubeLink = resultSet.getString("YoutubeLink");

                        searchResults .add(new Song(songName, artist, lyrics, youtubeLink));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return searchResults ;
    }
    
    private static List<Artist> searchArtists(String searchQuery) {
        List<Artist> searchResults  = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sqlQuery = "SELECT * FROM Artiste WHERE ArtistName LIKE ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, "%" + searchQuery + "%");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String ArtistName = resultSet.getString("ArtistName");
                        String ArtistRName = resultSet.getString("ArtistRName");
                        String ArtistBirthD = resultSet.getString("ArtistBirthD");
                        String ArtistBirthP = resultSet.getString("ArtistBirthP");
                        String ArtistInfo = resultSet.getString("ArtistInfo");
                        searchResults .add(new Artist(ArtistName, ArtistRName, ArtistInfo, ArtistBirthD, ArtistBirthP));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return searchResults ;
    }
    
    private static List<Song> getSongsByArtist(String searchQuery) {
        List<Song> searchResults = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sqlQuery = "SELECT * FROM Songs WHERE Artist LIKE ?";
            try (
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, "%" + searchQuery + "%");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String songName = resultSet.getString("Title");
                        String artist = resultSet.getString("Artist");
                        String lyrics = resultSet.getString("Lyrics");
                        String youtubeLink = resultSet.getString("YoutubeLink");

                        searchResults .add(new Song(songName, artist, lyrics, youtubeLink));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return searchResults;
    }
    
    private static String normalizeChar(String input) {
    // Loại bỏ dấu và chuyển đổi tất cả các ký tự thành chữ thường
        String normalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();

        return normalizedString;
    }
    
//    private static List<Artist> handleArtistSearch(String searchQuery) {
//        return searchArtists(searchQuery);
//    }
//    
//    private static List<Song> handleSongSearch(String searchQuery) {
//        return searchSongs(searchQuery);
//    }
}

class Song implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String lyrics;
    private String artist;
    private String youtubeLink;

    // Constructor
    public Song(String songName, String artist, String lyrics, String youtubeLink) {
        this.title = songName;
        this.artist = artist;
        this.lyrics = lyrics;
        this.youtubeLink = youtubeLink;
    }

    // Getter và Setter cho thuộc tính title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter và Setter cho thuộc tính lyrics
    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    // Getter và Setter cho thuộc tính artist
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    // Getter và Setter cho thuộc tính youtubeLink
    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }
}

class Artist implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ArtistName;
    private String ArtistRName;
    private String ArtistBirthD;
    private String ArtistBirthP;
    private String ArtistInfo;

    // Constructor
    public Artist(String ArtistName, String ArtistRName, String ArtistInfo, String ArtistBirthD, String ArtistBirthP) {
        this.ArtistName = ArtistName;
        this.ArtistRName = ArtistRName;
        this.ArtistBirthD = ArtistBirthD;
        this.ArtistBirthP = ArtistBirthP;
        this.ArtistInfo = ArtistInfo;
    }

    public String getArtistName() {
        return ArtistName;
    }

    public void setArtistName(String ArtistName) {
        this.ArtistName = ArtistName;
    }

    public String getArtistRName() {
        return ArtistRName;
    }

    public void setArtistRName(String ArtistRName) {
        this.ArtistRName = ArtistRName;
    }

    public String getArtistBirthD() {
        return ArtistBirthD;
    }

    public void setArtistBirthD(String ArtistBirthD) {
        this.ArtistBirthD = ArtistBirthD;
    }

    public String getArtistBirthP() {
        return ArtistBirthP;
    }

    public void setArtistBirthP(String ArtistBirthP) {
        this.ArtistBirthP = ArtistBirthP;
    }
    
    public String getArtistInfo() {
        return ArtistInfo;
    }

    public void setArtistInfo(String ArtistInfo) {
        this.ArtistInfo = ArtistInfo;
    }
}
