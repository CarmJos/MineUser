package cc.carm.lib.minecraft.user.tests;

import cc.carm.lib.minecraft.user.data.UserKey;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserKeyTest {

    public static final Random RANDOM = new Random();

    public static UserKey random() {
        return random(RANDOM.nextInt(100000));
    }

    public static UserKey random(int uid) {
        UUID uuid = UUID.randomUUID();
        return new UserKey(uid, uuid, uuid.toString().substring(0, 5));
    }

    public static Set<UserKey> bound(int size) {
        return IntStream.rangeClosed(1, size).mapToObj(UserKeyTest::random).collect(Collectors.toSet());
    }

    @Test
    public void testJSON() {

        String value = random().toJson();
        System.out.println(value);

        UserKey parsed = UserKey.parseJson(value);
        System.out.println(parsed.id() + " -> " + parsed.uuid() + " : " + parsed.name());
    }


    @Test
    public void testString() {

        String value = random().toString();
        System.out.println(value);

        UserKey parsed = UserKey.parse(value);
        System.out.println(parsed.id() + " -> " + parsed.uuid() + " : " + parsed.name());
    }


    @Test
    public void speed() {
        Set<UserKey> KEYS = bound(10000);

        long s1 = System.nanoTime();
        // toString time
        for (UserKey key : KEYS) {
            String s = key.toString();
        }
        System.out.println("toString: " + (System.nanoTime() - s1) + "ns");

        // Parse time
        List<String> parsed = KEYS.stream().map(UserKey::toString).toList();
        long s2 = System.nanoTime();
        for (String s : parsed) {
            UserKey.parse(s);
        }
        System.out.println("parse: " + (System.nanoTime() - s2) + "ns");

        // toJson time
        long s4 = System.nanoTime();
        for (UserKey key : KEYS) {
            String s = key.toJson();
        }
        System.out.println("toJson: " + (System.nanoTime() - s4) + "ns");

        // JSON time
        List<String> jsoned = KEYS.stream().map(UserKey::toJson).toList();
        long s3 = System.nanoTime();
        for (String s : jsoned) {
            UserKey.parseJson(s);
        }
        System.out.println("parseJSON: " + (System.nanoTime() - s3) + "ns");


    }
}
