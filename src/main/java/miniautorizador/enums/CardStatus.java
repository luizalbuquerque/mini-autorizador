package miniautorizador.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CardStatus {

    INATIVO( "INATIVO" ),
    ATIVO( "ATIVO" );

    private String value;

}
