package com.seoultoilet.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoultoilet.entity.Toilet;
import com.seoultoilet.repository.ToiletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ToiletRepository toiletRepository;
    private final ObjectMapper objectMapper;

    public DataInitializer(ToiletRepository toiletRepository, ObjectMapper objectMapper) {
        this.toiletRepository = toiletRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (toiletRepository.count() > 0) {
            return;
        }

        InputStream is = new ClassPathResource("data/toilets-seoul.json").getInputStream();
        List<Map<String, Object>> rawList = objectMapper.readValue(
                is, new TypeReference<List<Map<String, Object>>>() {});

        int count = 0;
        for (Map<String, Object> raw : rawList) {
            Toilet t = new Toilet();
            t.setName(getStr(raw, "화장실명"));
            t.setType(getStr(raw, "구분명"));
            t.setAddress(firstNonEmpty(
                    getStr(raw, "소재지도로명주소"),
                    getStr(raw, "소재지지번주소")));
            t.setLat(parseDouble(raw, "WGS84위도"));
            t.setLng(parseDouble(raw, "WGS84경도"));

            if (t.getLat() == null || t.getLng() == null) continue;
            if (t.getLat() == 0.0 && t.getLng() == 0.0) continue;

            t.setAgency(getStr(raw, "관리기관명"));
            t.setPhone(getStr(raw, "전화번호"));
            t.setHours(getStr(raw, "개방시간"));
            t.setHoursDetail(getStr(raw, "개방시간상세"));
            t.setMaleToilet(parseInt(raw, "남성용-대변기수"));
            t.setMaleUrinal(parseInt(raw, "남성용-소변기수"));
            t.setFemaleToilet(parseInt(raw, "여성용-대변기수"));
            t.setDisabledMale(parseInt(raw, "남성용-장애인용대변기수"));
            t.setDisabledFemale(parseInt(raw, "여성용-장애인용대변기수"));
            t.setEmergencyBell("Y".equals(getStr(raw, "비상벨설치여부")));
            t.setDiaperTable("Y".equals(getStr(raw, "기저귀교환대유무")));

            toiletRepository.save(t);
            count++;
        }
    }

    private String getStr(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString().trim() : null;
    }

    private Double parseDouble(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return null;
        try {
            return Double.parseDouble(v.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInt(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return 0;
        try {
            return Integer.parseInt(v.toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String firstNonEmpty(String... values) {
        for (String s : values) {
            if (s != null && !s.isEmpty()) return s;
        }
        return null;
    }
}
