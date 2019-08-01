package com.kuyun.coolweather.db;

import java.util.List;

public class Weather {

    public List<HeWeatherBean> HeWeather;

    public static class HeWeatherBean {
        /**
         * aqi : {"city":{"aqi":"59","pm25":"32","qlty":"良"}}
         * basic : {"admin_area":"江苏","cid":"cn101190401","city":"苏州","cnty":"中国","id":"cn101190401","lat":"26.07530212","location":"苏州","lon":"119.30623627","parent_city":"苏州","tz":"+8.00","update":{"loc":"2019-07-23 17:25","utc":"2019-07-23 09:25"}}
         * daily_forecast : [{"cond":{"txt_d":"中雨"},"date":"2019-07-24","tmp":{"max":"20","min":"15"}},{"cond":{"txt_d":"多云"},"date":"2019-07-25","tmp":{"max":"27","min":"14"}},{"cond":{"txt_d":"阴"},"date":"2019-07-26","tmp":{"max":"17","min":"10"}},{"cond":{"txt_d":"阵雨"},"date":"2019-07-27","tmp":{"max":"25","min":"15"}},{"cond":{"txt_d":"多云"},"date":"2019-07-28","tmp":{"max":"27","min":"14"}},{"cond":{"txt_d":"阴"},"date":"2019-07-29","tmp":{"max":"28","min":"10"}}]
         * msg : 所有天气数据均为模拟数据，仅用作学习目的使用，请勿当作真实的天气预报软件来使用。
         * now : {"cloud":"99","cond":{"code":"104","txt":"阴"},"cond_code":"104","cond_txt":"阴","fl":"16","hum":"84","pcpn":"0.0","pres":"1012","tmp":"17","vis":"6","wind_deg":"155","wind_dir":"东南风","wind_sc":"3","wind_spd":"12"}
         * status : ok
         * suggestion : {"comf":{"brf":"舒适","txt":"白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。","type":"comf"},"cw":{"brf":"不宜","txt":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。","type":"cw"},"sport":{"brf":"较不宜","txt":"有较强降水，建议您选择在室内进行健身休闲运动。","type":"sport"}}
         * update : {"loc":"2019-07-23 17:25","utc":"2019-07-23 09:25"}
         */

        public AqiBean aqi;
        public BasicBean basic;
        public String msg;
        public NowBean now;
        public String status;
        public SuggestionBean suggestion;
        public UpdateBeanX update;
        public List<DailyForecastBean> daily_forecast;

        public static class AqiBean {
            /**
             * city : {"aqi":"59","pm25":"32","qlty":"良"}
             */

            public CityBean city;

            public static class CityBean {
                /**
                 * aqi : 59
                 * pm25 : 32
                 * qlty : 良
                 */

                public String aqi;
                public String pm25;
                public String qlty;
            }
        }

        public static class BasicBean {
            /**
             * admin_area : 江苏
             * cid : cn101190401
             * city : 苏州
             * cnty : 中国
             * id : cn101190401
             * lat : 26.07530212
             * location : 苏州
             * lon : 119.30623627
             * parent_city : 苏州
             * tz : +8.00
             * update : {"loc":"2019-07-23 17:25","utc":"2019-07-23 09:25"}
             */

            public String admin_area;
            public String cid;
            public String city;
            public String cnty;
            public String id;
            public String lat;
            public String location;
            public String lon;
            public String parent_city;
            public String tz;
            public UpdateBean update;

            public static class UpdateBean {
                /**
                 * loc : 2019-07-23 17:25
                 * utc : 2019-07-23 09:25
                 */

                public String loc;
                public String utc;
            }
        }

        public static class NowBean {
            /**
             * cloud : 99
             * cond : {"code":"104","txt":"阴"}
             * cond_code : 104
             * cond_txt : 阴
             * fl : 16
             * hum : 84
             * pcpn : 0.0
             * pres : 1012
             * tmp : 17
             * vis : 6
             * wind_deg : 155
             * wind_dir : 东南风
             * wind_sc : 3
             * wind_spd : 12
             */

            public String cloud;
            public CondBean cond;
            public String cond_code;
            public String cond_txt;
            public String fl;
            public String hum;
            public String pcpn;
            public String pres;
            public String tmp;
            public String vis;
            public String wind_deg;
            public String wind_dir;
            public String wind_sc;
            public String wind_spd;

            public static class CondBean {
                /**
                 * code : 104
                 * txt : 阴
                 */

                public String code;
                public String txt;
            }
        }

        public static class SuggestionBean {
            /**
             * comf : {"brf":"舒适","txt":"白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。","type":"comf"}
             * cw : {"brf":"不宜","txt":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。","type":"cw"}
             * sport : {"brf":"较不宜","txt":"有较强降水，建议您选择在室内进行健身休闲运动。","type":"sport"}
             */

            public ComfBean comf;
            public CwBean cw;
            public SportBean sport;

            public static class ComfBean {
                /**
                 * brf : 舒适
                 * txt : 白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。
                 * type : comf
                 */

                public String brf;
                public String txt;
                public String type;
            }

            public static class CwBean {
                /**
                 * brf : 不宜
                 * txt : 不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。
                 * type : cw
                 */

                public String brf;
                public String txt;
                public String type;
            }

            public static class SportBean {
                /**
                 * brf : 较不宜
                 * txt : 有较强降水，建议您选择在室内进行健身休闲运动。
                 * type : sport
                 */

                public String brf;
                public String txt;
                public String type;
            }
        }

        public static class UpdateBeanX {
            /**
             * loc : 2019-07-23 17:25
             * utc : 2019-07-23 09:25
             */

            public String loc;
            public String utc;
        }

        public static class DailyForecastBean {
            /**
             * cond : {"txt_d":"中雨"}
             * date : 2019-07-24
             * tmp : {"max":"20","min":"15"}
             */

            public CondBeanX cond;
            public String date;
            public TmpBean tmp;

            public static class CondBeanX {
                /**
                 * txt_d : 中雨
                 */

                public String txt_d;
            }

            public static class TmpBean {
                /**
                 * max : 20
                 * min : 15
                 */

                public String max;
                public String min;
            }
        }
    }
}
