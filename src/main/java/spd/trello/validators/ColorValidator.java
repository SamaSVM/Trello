package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Color;
import spd.trello.exception.BadRequestException;

@Component
public class ColorValidator extends AbstractValidator<Color> {


    @Override
    public void validateSaveEntity(Color entity) {
        StringBuilder exceptions = new StringBuilder();
        checkColorFields(exceptions, entity);
        throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Color entity) {
        StringBuilder exceptions = new StringBuilder();
        checkColorFields(exceptions, entity);
        throwException(exceptions);
    }

    private void checkColorFields(StringBuilder exceptions, Color entity) {
        if (entity.getRed() == null || entity.getGreen() == null || entity.getBlue() == null){
            throw new BadRequestException("Fields red, green and blue must be filled!");
        }
        if(entity.getRed() < 0 || entity.getRed() > 255){
            exceptions.append("The red color should be in the range 0 to 255. \n");
        }
        if(entity.getGreen() < 0 || entity.getGreen() > 255){
            exceptions.append("The green color should be in the range 0 to 255. \n");
        }
        if(entity.getBlue() < 0 || entity.getBlue() > 255){
            exceptions.append("The blue color should be in the range 0 to 255. \n");
        }
    }


    private void throwException(StringBuilder exceptions) {
        if (exceptions.length() != 0) {
            throw new BadRequestException(exceptions.toString());
        }
    }
}
