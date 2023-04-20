package com.tintin3274.wordbansay.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateScore {
    private List<String> killList;
    private List<Integer> guessList;
    private List<Integer> deadList;
}
