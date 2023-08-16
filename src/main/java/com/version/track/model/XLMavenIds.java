package com.version.track.model;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelRow;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class XLMavenIds {

    @ExcelRow
    private int rowIndex;

    @ExcelCell(0)
    private String id;

    @ExcelCell(5)
    private String name;
}