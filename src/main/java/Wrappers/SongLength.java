package Wrappers;

import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SongLength {

    private int seconds, minutes, hours, days, years;
    private int[] timeFields = new int[5];
    private long milliseconds;

    public SongLength(@Positive long mSeconds){
        boolean isZero = false;
        int unit = 60;
        this.milliseconds = mSeconds;
        this.timeFields[0] = (int)Math.floorDiv(this.milliseconds, 1000L);
        for(int i = 1; i < 5; ++i){
            if(!isZero) {
                if(i > 2){
                    if(i == 3){
                        unit = 24;
                    }else{
                        unit = 365;
                    }
                }
                this.timeFields[i] = Math.floorDiv(this.timeFields[i-1], unit);
                this.timeFields[i-1] -= this.timeFields[i] * unit;
                if(this.timeFields[i] == 0){
                    isZero = true;
                }
            }else{
                this.timeFields[i] = this.timeFields[i-1];
            }
        }
    }

    @Override
    public String toString(){
        StringBuilder dateBuilder = new StringBuilder();
        for(int i = timeFields.length - 1; i >= 0; --i){
            if(timeFields[i] != 0 || ( i != 4 && timeFields[i] == 0 && timeFields[i+1] != 0)){
                if(timeFields[i] == 0){
                    dateBuilder.append("00");
                }else {
                    dateBuilder.append(timeFields[i]);
                }
                if(i != 0){
                    dateBuilder.append(":");
                }
            }
        }
        return dateBuilder.toString();
    }
}
