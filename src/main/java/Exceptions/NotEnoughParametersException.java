package Exceptions;

import org.jetbrains.annotations.NotNull;

public class NotEnoughParametersException extends Exception{

    private final String requiredParams;
    private final String issuedComnmand;

    public NotEnoughParametersException(@NotNull String issuedCommand, @NotNull String requiredParams){
        super();
        this.requiredParams = requiredParams;
        this.issuedComnmand = issuedCommand;
    }

    @Override
    public String getMessage(){
        StringBuilder builder = new StringBuilder();
        builder.append("Not enough parameters: ")
                .append(this.issuedComnmand)
                .append(" requires ")
                .append(this.requiredParams)
                .append(" parameters");
        return builder.toString();
    }

}
