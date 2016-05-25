package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.FileManager;
import model.FileManagerInterface;
import model.LeftEditorFileNotFoundException;
import model.RightEditorFileNotFoundException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by SH on 2016-05-18.
 */
public class ControlPaneSceneController {

    @FXML private HighlightEditorInterface editor;

    @FXML private Button btnCompare, btnMergeLeft, btnMergeRight;
    @FXML private GridPane controlPane;
    private HighlightEditorInterface leftEditor, rightEditor;


    @FXML
    public void initialize(){
        Platform.runLater(()->
            _getEditorReference()
        );
    }

    private void _getEditorReference(){
        Node root = controlPane.getScene().getRoot();
        this.leftEditor  = (HighlightEditorInterface)((BorderPane)root.lookup("#leftEditor")).getCenter().lookup("#editor");
        this.rightEditor = (HighlightEditorInterface)((BorderPane)root.lookup("#rightEditor")).getCenter().lookup("#editor");
    }


    @FXML // 비교 버튼이 클릭되었을 때의 동작
    private void onBtnCompareClicked(ActionEvent event) {
        ArrayList<String> leftList;
        ArrayList<String> rightList;
        try {
            if((!FileManager.getFileManagerInterface().getEdited(FileManagerInterface.SideOfEditor.Left)) &&
                    (!FileManager.getFileManagerInterface().getEdited(FileManagerInterface.SideOfEditor.Right))) {
                FileManager.getFileManagerInterface().setCompare();
                this.leftEditor.update(FileManagerInterface.SideOfEditor.Left);
                this.rightEditor.update(FileManagerInterface.SideOfEditor.Right);
            }
            else {
                try {
                    FileManager.getFileManagerInterface().saveFile(editor.getText(), FileManagerInterface.SideOfEditor.Left);
                } catch (FileNotFoundException e) {
                    boolean condition = true;
                    while(condition) {
                        try {
                            FileExplorer fileExplorer = new FileSaveExplorer();
                            FileManager.getFileManagerInterface().saveFile(editor.getText(), fileExplorer.getDialog(btnCompare).getAbsolutePath(), FileManagerInterface.SideOfEditor.Left);
                            condition = false;
                        } catch (FileNotFoundException e1) { }
                    }
                }
                try {
                    FileManager.getFileManagerInterface().saveFile(editor.getText(), FileManagerInterface.SideOfEditor.Right);
                } catch (FileNotFoundException e) {
                    boolean condition = true;
                    while(condition) {
                        try {
                            FileExplorer fileExplorer = new FileSaveExplorer();
                            FileManager.getFileManagerInterface().saveFile(editor.getText(), fileExplorer.getDialog(btnCompare).getAbsolutePath(), FileManagerInterface.SideOfEditor.Right);
                            condition = false;
                        } catch (FileNotFoundException e1) { }
                    }
                }
            }
        } catch (LeftEditorFileNotFoundException e) {
            e.printStackTrace();
        } catch (RightEditorFileNotFoundException e) {
            e.printStackTrace();
        }
            //TODO 블럭에 속하는 line들을 highlight한다.
    }

    //TODO Merge
    //어떤 블록이 선택되었는지 알아낼 수 있어야 한다.

    /*
    * right의 경우: 오른족 패널의 선택된 블럭의 정보를 왼쪽 패널로 대체한다.
    * 활용: edit pane에 연결된 file의 contents를 가지고 있는 line class를 저장하는 arrayList, fileManager활용
    * isCompare이 true인 상태
    * merge하면 compare가 꺼진다.
    * edit랑 merge가 될 경우는 isCompared를 false라고 .. 그 때의 버튼 비활성화 여부 체크해주기
    * isCompare == true면,,, EditPane에서 블럭 선택 가능-선택된곳은 다른색으로 표시..
    * 한쪽블럭선택하면 해당다른곳도 같이 선택된다.
    * 선택된 블럭을 다시 클릭하면 선택 되기 전 색으로 돌아간다.
    * Merge버튼 활성화 조건 1)양 EditPane에 FileLoad되어있음
    *                       2)isCompared가 true인 경우
    * */
    @FXML // merge-to-right 버튼이 클릭되었을 때의 동작
    private void onBtnMergeToRightClicked(ActionEvent event) {
        //TODO 위 컴페어 버튼처럼 왼쪽, 오른쪽 파일을 저장중인 리스트를 받아야 함.
        //TODO 선택된 블럭 존재 여부에 따라 분기
        //TODO 있다면 복사. 없다면 메서드 종료
    }

    @FXML // merge-to-left 버튼이 클릭되었을 때의 동작
    private void onBtnMergeToLeftClicked(ActionEvent event) {
        //TODO 위 컴페어 버튼처럼 왼쪽, 오른쪽 파일을 저장중인 리스트를 받아야 함.
    }

    @FXML
    private void onMenuBtnResetClicked(ActionEvent event) {

    }

    @FXML
    private void onMenuBtnCloseClicked(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onMenuBtnCopyToRightClicked(ActionEvent event) {
        onBtnMergeToRightClicked(event);
    }

    @FXML
    private void onMenuBtnCopyToLeftClicked(ActionEvent event) {
        onBtnMergeToLeftClicked(event);
    }

    @FXML
    private void onMenuBtnCopyToCompareClicked(ActionEvent event) {
        onBtnCompareClicked(event);
    }

    @FXML
    private void onMenuBtnHelpClicked(ActionEvent event) {
    }

    @FXML
    private void onMenuBtnAboutClicked(ActionEvent event) {
    }

}
