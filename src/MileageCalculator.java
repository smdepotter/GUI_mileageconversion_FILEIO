/**
 * Programmer: Sean M. DePotter
 * Filename: MileageCalculator.java
 * Due Date: 04/15/2016
 * Description: Individual Assignment #2 - MileageCalculator Class
 */
public class MileageCalculator {

    public double convertMpgToLiters(double mpg){
        //performs conversion to lpg from mpg, gives back lpg double
        double lpgFromMpg;
        lpgFromMpg = 235.215/mpg;

        return lpgFromMpg;
    }

    public double convertLitersToMpg(double liters){
        //performs conversion from mpg to lpg, gives back mpg double
        double mpgFromLpg;
        mpgFromLpg = 235.215/liters;
        return mpgFromLpg;
    }

    public String determineRating(double mpg){
        String rating;

        //conduct test based on mpg from user's choice and star rating
        // based on the value of mpg. Higher *'s = Higher mpg, gives program back the star value in a string
        if (mpg >= 27){
            rating = "****";
        }
        else if (mpg >= 24 && mpg <27){
            rating = "***";
        }
        else if (mpg >=21 && mpg <24){
            rating = "**";
        }
        else if (mpg >= 18 && mpg <21){
            rating = "*";
        }
        else {
            rating= "";
        }

        return rating;
    }
}
