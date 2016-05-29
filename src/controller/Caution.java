package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.FileManager;
import model.FileManagerInterface;

import java.util.Optional;

/**
 * Created by 효민 on 2016-05-23.
 */
public class Caution {

    private ButtonType buttonTypeSave = new ButtonType("저장");
    private ButtonType buttonTypeNotSave = new ButtonType("저장 안 함");
    private ButtonType buttonTypeClose = new ButtonType("닫기");


    public ButtonType getSavebtn(){
        return buttonTypeSave;
    }

    public Optional<ButtonType> getSaveWindow(FileManagerInterface.SideOfEditor side){

        Alert alert = new Alert(Alert.AlertType.WARNING);
        if(side == FileManagerInterface.SideOfEditor.Left)
            alert.setTitle("왼쪽 파일 저장");
        else alert.setTitle("오른쪽 파일 저장");
        alert.setHeaderText(null);
        if(FileManager.getFileManagerInterface().getFilePath(side) == null)
            alert.setContentText("새로운 파일로 저장하시겠습니까?");
        else
            alert.setContentText("변경 사항을 저장하시겠습니까?");

        alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeNotSave);

        return alert.showAndWait();
    }

    public void noticeSaveWindow(FileManagerInterface.SideOfEditor side) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if(side == FileManagerInterface.SideOfEditor.Left)
            alert.setTitle("왼쪽 파일 저장");
        else alert.setTitle("오른쪽 파일 저장");
        alert.setHeaderText(null);
        if(side == FileManagerInterface.SideOfEditor.Left)
            alert.setContentText("Left side File - 저장에 실패하였습니다.");
        else
            alert.setContentText("Right side File - 저장에 실패하였습니다.");
        alert.getButtonTypes().setAll(buttonTypeClose);
        alert.showAndWait();
    }


    public void noticeMergeFailure(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Simple Merge - 소공 2팀");
        alert.setHeaderText(null);
        alert.setContentText("복사에 실패하였습니다.");

        alert.showAndWait();
    }

}
