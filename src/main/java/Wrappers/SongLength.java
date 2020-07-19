package Wrappers;

import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Positive;

public class SongLength {

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

    public SongLength(@NotNull String length) throws IllegalArgumentException{
        String[] params = length.split(":");
        if(params.length == 0 || params.length > 5){
            throw new IllegalArgumentException();
        }
        for(int i = params.length -1 ; i >= 0; --i){
            this.timeFields[params.length - 1 - i] = Integer.parseInt(params[i]);
        }
    }

    public long toMilliseconds(){
        Long milliseconds = 0L;
        int unit = 365;
        for(int i = this.timeFields.length -1; i >= 0; --i){
            switch (i){
                case 3:
                    unit = 24;
                    break;
                case 2:
                    unit = 60;
                    break;
                case 0:
                    unit = 1000;
                    break;
            }
            if(this.timeFields[i] != 0){
                milliseconds += this.timeFields[i];
            }
            milliseconds *= unit;
        }
        return milliseconds;
    }

    @Override
    public String toString(){
        StringBuilder dateBuilder = new StringBuilder();
        for(int i = timeFields.length - 1; i >= 0; --i){
            if(timeFields[i] != 0 || ( i != 4 && timeFields[i] == 0 && timeFields[i+1] != 0)){
                if(timeFields[i] == 0){
                    dateBuilder.append("00");
                }else {
                    dateBuilder.append(timeFields[i] < 10?"0" + timeFields[i]:timeFields[i]);
                }
                if(i != 0){
                    dateBuilder.append(":");
                }
            }
        }
        return dateBuilder.toString();
    }
}
