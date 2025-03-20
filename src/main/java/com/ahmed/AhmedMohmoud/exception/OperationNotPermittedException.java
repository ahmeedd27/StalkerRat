package com.ahmed.AhmedMohmoud.exception;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OperationNotPermittedException extends RuntimeException{
   public OperationNotPermittedException(String errMsg){
       super(errMsg);
    }
}
