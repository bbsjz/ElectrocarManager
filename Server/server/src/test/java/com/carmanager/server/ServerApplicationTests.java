package com.carmanager.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootTest
@EnableJpaRepositories(basePackages = {"com.carmanager.server.Dao"})
class ServerApplicationTests {
        @Test
        void gsonTest()
        {
//            MovingDto movingDto=new MovingDto();
//            movingDto.setAlert(false);
//            movingDto.setId(1L);
//            movingDto.setBeginTime(new Date());
//            movingDto.setEndTime(new Date());
//            movingDto.setFromLatitude(1.0);
//            movingDto.setFromLongitude(1.0);
//            movingDto.setToLatitude(1.0);
//            movingDto.setToLatitude(1.0);
//            Gson gson=new Gson();
//            String s=gson.toJson(movingDto);
//            String s1="["+s+","+s+"]";
//            List<MovingDto> list=gson.fromJson(s1, new TypeToken<List<MovingDto>>(){}.getType());
//            System.out.println(list.get(0).getBeginTime());
//            String json="{\"status\":0,\"result\":{\"location\":{\"lng\":115.89999999999994,\"lat\":30.49999992293362},\"formatted_address\":\"湖北省黄冈市蕲春县\",\"business\":\"\",\"addressComponent\":{\"country\":\"中国\",\"country_code\":0,\"country_code_iso\":\"CHN\",\"country_code_iso2\":\"CN\",\"province\":\"湖北省\",\"city\":\"黄冈市\",\"city_level\":2,\"district\":\"蕲春县\",\"town\":\"\",\"town_code\":\"\",\"distance\":\"\",\"direction\":\"\",\"adcode\":\"421126\",\"street\":\"\",\"street_number\":\"\"},\"pois\":[{\"addr\":\"黄冈市蕲春县\",\"cp\":\" \",\"direction\":\"东\",\"distance\":\"88\",\"name\":\"王家寨\",\"poiType\":\"行政地标\",\"point\":{\"x\":115.89925111476436,\"y\":30.500240078428008},\"tag\":\"行政地标;村庄\",\"tel\":\"\",\"uid\":\"bec532d8ba133f5f2806ce92\",\"zip\":\"\",\"parent_poi\":{\"name\":\"\",\"tag\":\"\",\"addr\":\"\",\"point\":{\"x\":0.0,\"y\":0.0},\"direction\":\"\",\"distance\":\"\",\"uid\":\"\"}},{\"addr\":\"黄冈市蕲春县\",\"cp\":\" \",\"direction\":\"南\",\"distance\":\"383\",\"name\":\"大垄\",\"poiType\":\"行政地标\",\"point\":{\"x\":115.89900857227666,\"y\":30.502853854804454},\"tag\":\"行政地标;村庄\",\"tel\":\"\",\"uid\":\"8190d5331cb238b6c1f1cfdf\",\"zip\":\"\",\"parent_poi\":{\"name\":\"\",\"tag\":\"\",\"addr\":\"\",\"point\":{\"x\":0.0,\"y\":0.0},\"direction\":\"\",\"distance\":\"\",\"uid\":\"\"}},{\"addr\":\"湖北省黄冈市蕲春县\",\"cp\":\" \",\"direction\":\"东北\",\"distance\":\"536\",\"name\":\"杨家塆\",\"poiType\":\"行政地标\",\"point\":{\"x\":115.89761619873619,\"y\":30.496373737457433},\"tag\":\"行政地标;村庄\",\"tel\":\"\",\"uid\":\"04bb37c264031dcecac3db63\",\"zip\":\"\",\"parent_poi\":{\"name\":\"\",\"tag\":\"\",\"addr\":\"\",\"point\":{\"x\":0.0,\"y\":0.0},\"direction\":\"\",\"distance\":\"\",\"uid\":\"\"}},{\"addr\":\"黄冈市蕲春县\",\"cp\":\" \",\"direction\":\"东\",\"distance\":\"979\",\"name\":\"上河村\",\"poiType\":\"行政地标\",\"point\":{\"x\":115.89136399238675,\"y\":30.501461405386619},\"tag\":\"行政地标;村庄\",\"tel\":\"\",\"uid\":\"375c0c7c1f69e0aa4e5547c4\",\"zip\":\"\",\"parent_poi\":{\"name\":\"\",\"tag\":\"\",\"addr\":\"\",\"point\":{\"x\":0.0,\"y\":0.0},\"direction\":\"\",\"distance\":\"\",\"uid\":\"\"}}],\"roads\":[],\"poiRegions\":[],\"sematic_description\":\"王家寨东88米\",\"cityCode\":271}}";
//            try {
//                JSONObject jsonObject= new JSONObject(json);
//                if(jsonObject.optInt("status")==0)
//                {
//                    System.out.println("haha");
//                }
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//            long du=1;
//            System.out.println(DateUtils.convertMillis(du));
                System.out.println("{"+"\"id\":"+1+","+"\"distance\":"+88.0+"}");
        }
}
