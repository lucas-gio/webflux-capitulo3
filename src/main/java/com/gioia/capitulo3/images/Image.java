package com.gioia.capitulo3.images;

import com.mongodb.BasicDBObject;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
class Image {
    @Id private String id;
    private String name;

    private Image(String name){
        this.id = new ObjectId().toString();
        this.name = name;
    }

    static Image withName(String name){
        return new Image(name);
    }
}
