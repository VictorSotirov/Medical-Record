package com.example.medical_record.exceptions.diagnosis;

public class DiagnosisAlreadyExistsException extends Exception
{
    public DiagnosisAlreadyExistsException(String message)
    {
        super(message);
    }
}
