package com.yjkmust.arcgisdemo.Bean;

import java.util.List;

/**
 * Created by GEOFLY on 2017/8/22.
 */

public class CityJsonBean {

    private List<StateBean> state;

    public List<StateBean> getState() {
        return state;
    }

    public void setState(List<StateBean> state) {
        this.state = state;
    }

    public static class StateBean {
        /**
         * name : 昆明市
         * code : 01
         * city : [{"name":"五华区","code":"02"},{"name":"盘龙区","code":"03"},{"name":"官渡区","code":"11"},{"name":"西山区","code":"12"},{"name":"东川区","code":"13"},{"name":"呈贡县","code":"21"},{"name":"晋宁县","code":"22"},{"name":"富民县","code":"24"},{"name":"宜良县","code":"25"},{"name":"石林县","code":"26"},{"name":"嵩明县","code":"27"},{"name":"禄劝县","code":"28"},{"name":"寻甸县","code":"29"},{"name":"安宁市","code":"81"}]
         */

        private String name;
        private String code;
        private List<CityBean> city;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public List<CityBean> getCity() {
            return city;
        }

        public void setCity(List<CityBean> city) {
            this.city = city;
        }

        public static class CityBean {
            /**
             * name : 五华区
             * code : 02
             */

            private String name;
            private String code;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }
        }
    }
}
