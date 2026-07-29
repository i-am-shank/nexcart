package com.springProjects.onlineStore.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ScrollPositionUtil {
    private static final Logger logger = LoggerFactory.getLogger(ScrollPositionUtil.class);

    /**
     * Fetches a ScrollPosition (initial), or starting with encodedPosition
     * decodedPosition will be like  :  "key1=val1,key2=val2,.."
     *
     * @param encodedPosition
     * @return
     */
    public static ScrollPosition decode(String encodedPosition) {
        if(!StringUtils.hasLength(encodedPosition)) {
            // creates initial ScrollPosition (to start scrolling using keySet-queries)
            return ScrollPosition.keyset();
        }
        try {
            String decodedPosition = new String(Base64.getDecoder().decode(encodedPosition));
            String[] pairs = decodedPosition.split(",");

            Map<String, Object> keys = new HashMap<>();
            for(String pair : pairs) {
                // pair = "key3=val3"
                String[] keyValue = pair.split("=");
                if(keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];

                    // Try to parse as Long first, if failed -> store as String
                    try {
                        keys.put(key, Long.parseLong(value));
                    } catch (NumberFormatException exception) {
                        logger.error("Error in scrollPosition value parsing : " + exception.getMessage(), exception);
                        keys.put(key, value);
                    }
                }
            }
            // Create a new ScrollPosition, from a key-set (keys) scrolling forward
            return ScrollPosition.forward(keys);
        } catch (Exception exception) {
            logger.error("Error in parsing encodedPosition : " + exception.getMessage(), exception);
            // Create initial ScrollPosition - scrolling from start
            return ScrollPosition.keyset();
        }
    }
}
