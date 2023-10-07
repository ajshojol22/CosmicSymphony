package com.jol.cosmicsymphony.data;

import java.util.Map;

public class WaterBodiesInfo {
    private Map<String, LakeInfo> water_bodies;

    public Map<String, LakeInfo> getWater_bodies() {
        return water_bodies;
    }

    public void setWater_bodies(Map<String, LakeInfo> water_bodies) {
        this.water_bodies = water_bodies;
    }

    public static class LakeInfo {
        private Map<String, EndangeredSpecies> endangered_species;
        private Location location;
        private WaterQuality water_quality;

        public Map<String, EndangeredSpecies> getEndangered_species() {
            return endangered_species;
        }

        public void setEndangered_species(Map<String, EndangeredSpecies> endangered_species) {
            this.endangered_species = endangered_species;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public WaterQuality getWater_quality() {
            return water_quality;
        }

        public void setWater_quality(WaterQuality water_quality) {
            this.water_quality = water_quality;
        }
    }

    public static class EndangeredSpecies {
        private String imageUrl;
        private String more_info;
        private String reason;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getMore_info() {
            return more_info;
        }

        public void setMore_info(String more_info) {
            this.more_info = more_info;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class Location {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    public static class WaterQuality {
        private int conductance;
        private double do_value;
        private double ph;
        private int temp;
        private double turbidity;

        public int getConductance() {
            return conductance;
        }

        public void setConductance(int conductance) {
            this.conductance = conductance;
        }

        public double getDo_value() {
            return do_value;
        }

        public void setDo_value(double do_value) {
            this.do_value = do_value;
        }

        public double getPh() {
            return ph;
        }

        public void setPh(double ph) {
            this.ph = ph;
        }

        public int getTemp() {
            return temp;
        }

        public void setTemp(int temp) {
            this.temp = temp;
        }

        public double getTurbidity() {
            return turbidity;
        }

        public void setTurbidity(double turbidity) {
            this.turbidity = turbidity;
        }
    }
}
