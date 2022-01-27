package co.empathy.academy.search.services.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TabTitleParser {

    public Map<String, Object> parseTitle(String line, Optional<Map<String, String>> ratings) {
        Map<String, Object> map = new HashMap<>();
        String[] values = line.split("\t");
        map.put("tConst", validateStr(values[0]));
        map.put("titleType", validateStr(values[1]));
        map.put("primaryTitle", validateStr(values[2]));
        map.put("originalTitle", validateStr(values[3]));
        map.put("isAdult", validateBool(values[4]));
        map.put("startYear", validateInt(values[5]));
        map.put("endYear", validateInt(values[6]));
        map.put("runtimeMinutes", validateInt(values[7]));
        map.put("genres", genresToList(values[8]));
        if (ratings.isPresent()) {
            Map<String, String> ratingsMap = ratings.get();
            String value = ratingsMap.get(validateStr(values[0]));
            if (value != null) {
                String[] ratingValues = value.split("\t");
                map.put("averageRating", validateFloat(ratingValues[0]));
                map.put("numVotes", validateInt(ratingValues[1]));
            }
            else {
                map.put("averageRating", null);
                map.put("numVotes", null);
            }
        }
        return map;
    }

    private String validateStr(String str) {
        return str.equals("\\N") ? null : str;
    }

    private Boolean validateBool(String str) {
        return str.equals("\\N") ? null : str.equals("1");
    }

    private Integer validateInt(String str) {
        return str.equals("\\N") ? null : Integer.parseInt(str);
    }

    private List<String> genresToList (String str) {
        return str.equals("\\N") ? List.of() : List.of(str.split(","));
    }

    private Float validateFloat(String str) {
        return str.equals("\\N") ? null : Float.parseFloat(str);
    }

}
