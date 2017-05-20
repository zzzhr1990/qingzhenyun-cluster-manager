package com.qingzhenyun.entity;

import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * MA
 * Created by guna on 2017/5/20.
 */
@Data
public class Magnet {

    public static Magnet parse(String magnetUri) throws ParseException, UnsupportedEncodingException, URISyntaxException {
        if (!magnetUri.startsWith("magnet:?")) throw new ParseException(magnetUri, 0);
        magnetUri = magnetUri.substring(8);
        Magnet magnet = new Magnet();
        String[] args = magnetUri.split("&");
        for (String arg : args) {
            if (arg.startsWith("xt=urn:btih:")) {
                magnet.infoHash = arg.substring(12).toLowerCase();
            } else if (arg.startsWith("dn=")) {
                magnet.name = URLDecoder.decode(arg.substring(3), "UTF-8");
            } else if (arg.startsWith("tr=")) {
                magnet.addAnnounce(new URI(URLDecoder.decode(arg.substring(3), "UTF-8")));
            }
        }
        return magnet;
    }

    private void addAnnounce(URI uri) {
        if (announce == null) announce = new ArrayList<>();
        announce.add(uri);
    }

    private String infoHash;
    private String name;
    private List<URI> announce;

}
