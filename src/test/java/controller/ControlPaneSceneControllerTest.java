package controller;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import mockInterface.FileDialogInterface;
import model.FileManager;
import model.FileManagerInterface;
import model.LineInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;
import utils.FxImageComparison;
import utils.TestUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Created by SH on 2016-06-03.
 */
public class ControlPaneSceneControllerTest extends ApplicationTest implements FxImageComparison {

    private Stage s;

    @Override
    public void init() throws Exception {
        FxToolkit.registerStage(Stage::new);
    }

    @Override
    public void start(Stage stage) {
        s = TestUtils.startStage(stage);
    }

    @Override
    public void stop() throws Exception {
        FxToolkit.cleanupStages();
        FxToolkit.hideStage();
    }

    @Before
    public void setUp() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
    }

    @Test
    public void controlPaneSceneInitialButtonClickTest(){
        Node[] buttons = { find("#btnMergeLeft"), find("#btnMergeRight"), find("#btnCompare") };

        // btnMergeLeft, btnMergeRight는 비활성화 상태이다.
        verifyThat(buttons[0], NodeMatchers.isDisabled());
        verifyThat(buttons[1], NodeMatchers.isDisabled());
        verifyThat(buttons[2], NodeMatchers.isEnabled());
    }

    @Test
    public void controlPaneSceneInitialMenuItemClickTest(){
        Node[] menuItems = { find("#menuFile"), find("#menuEdit"), find("#menuHelps") };

        // 처음에는 모든 메뉴아이템이 활성화 상태이다.
        for(Node node : menuItems){
            clickOn(node);
            verifyThat(node, NodeMatchers.isEnabled());
        }

    }

    @Test
    public void controlPaneSceneShortCutKeyClickTest(){

        final KeyCode SHORTCUT = System.getProperty("os.name").toLowerCase().contains("mac") ? KeyCode.COMMAND : KeyCode.CONTROL;

        push(SHORTCUT, KeyCode.RIGHT); // Copy to right
        WaitForAsyncUtils.waitForFxEvents();

        // 불가능한 창이 하나 떠야되는데 안뜨면 이상한거다.
        assertEquals(2, listWindows().size());
        closeCurrentWindow();

        push(SHORTCUT, KeyCode.LEFT); // Copy to left
        WaitForAsyncUtils.waitForFxEvents();

        // 불가능한 창이 하나 떠야되는데 안뜨면 이상한거다.
        assertEquals(2, listWindows().size());

    }

    @Test
    public void controlPaneSceneButtonCompareClickTest() throws IOException {

        Node btnCompare = find("#btnCompare");

        String leftFile = getClass().getResource("../test1-1.txt").getPath();
        String rightFile = getClass().getResource("../test1-2.txt").getPath();

        // OS 종속적인 친구들.
        FileDialogInterface fileDialogMock = createMock(FileDialogInterface.class);
        expect(fileDialogMock.getPath(FileManagerInterface.SideOfEditor.Left)).andReturn(leftFile);
        expect(fileDialogMock.getPath(FileManagerInterface.SideOfEditor.Right)).andReturn(rightFile);
        replay(fileDialogMock);

        Platform.runLater(()->{
            try {
                FileManager.getFileManagerInterface().loadFile(
                        fileDialogMock.getPath(FileManagerInterface.SideOfEditor.Right),
                        FileManagerInterface.SideOfEditor.Right
                );

                FileManager.getFileManagerInterface().loadFile(
                        fileDialogMock.getPath(FileManagerInterface.SideOfEditor.Left),
                        FileManagerInterface.SideOfEditor.Left
                );

                verify(fileDialogMock);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        clickOn(btnCompare);

        Node leftSplit = s.getScene().lookup("#leftSplit");
        Node rightSplit = s.getScene().lookup("#rightSplit");

        HighlightEditorInterface leftEditor = (HighlightEditorInterface) leftSplit.lookup("#editor");
        HighlightEditorInterface rightEditor = (HighlightEditorInterface) rightSplit.lookup("#editor");

        verifyThat(leftEditor.getHighlightListView(), (ListView<LineInterface> listView) -> {
            int i = 0;
            LineInterface item = listView.getItems().get(i);


            switch (i){
                case 0:
                case 19: return item.getHighlight() == LineInterface.LineHighlight.whitespace;
                case 1:
                case 20: return item.getHighlight() == LineInterface.LineHighlight.isDifferent;
            }

            return item.getHighlight() == LineInterface.LineHighlight.unHighlighted;
        });

        verifyThat(rightEditor.getHighlightListView(), (ListView<LineInterface> listView) -> {
            int i = 0;
            LineInterface item = listView.getItems().get(i);

            switch (i){
                case 0:
                case 19: return item.getHighlight() == LineInterface.LineHighlight.isDifferent;
                case 1:
                case 20: return item.getHighlight() == LineInterface.LineHighlight.whitespace;
            }

            return item.getHighlight() == LineInterface.LineHighlight.unHighlighted;
        });

        // view Rendering Testing
        Node tPane = find("#tabPane");
        assertNotNull("tabPane is null", tPane);
        assertSnapshotsEqual(getClass().getResource("../compareResult.png").getPath(), tPane, 1);
        assertEquals(FileManager.getFileManagerInterface().getComparing(), true);
    }

    @Test
    public void singleBlockSelectTest() throws IOException {
        controlPaneSceneButtonCompareClickTest();

        clickOn(".isDifferent");

        Node tPane = find("#tabPane");
        assertNotNull("tabPane is null", tPane);
        assertSnapshotsEqual(getClass().getResource("../singleSelectedResult.png").getPath(), tPane, 1);
    }

    @Test
    public void manyBlockSelectTest() throws IOException {
        controlPaneSceneButtonCompareClickTest();

        clickOn(".isDifferent");
        clickOn(".isDifferent");

        Node tPane = find("#tabPane");
        assertNotNull("tabPane is null", tPane);
        assertSnapshotsEqual(getClass().getResource("../multiSelectedResult.png").getPath(), tPane, 1);

    }

    @Test
    public void manyBlockMergeTest() throws IOException {
        //given :
        controlPaneSceneButtonCompareClickTest();

        //when :
        clickOn(".isDifferent");
        clickOn(".isDifferent");
        clickOn("#btnMergeRight");

        Node tPane = find("#tabPane");
        assertNotNull("tabPane is null", tPane);

        Node leftSplit = s.getScene().lookup("#leftSplit");
        Node rightSplit = s.getScene().lookup("#rightSplit");

        HighlightEditorInterface leftEditor = (HighlightEditorInterface) leftSplit.lookup("#editor");
        HighlightEditorInterface rightEditor = (HighlightEditorInterface) rightSplit.lookup("#editor");

        //then :

            // UI Testing
        assertSnapshotsEqual(getClass().getResource("../multiMergeResult.png").getPath(), tPane, 1);

            // System Testing
        assertEquals(leftEditor.getHighlightListView().getItems().get(0), rightEditor.getHighlightListView().getItems().get(0));
        assertEquals(leftEditor.getHighlightListView().getItems().get(1), rightEditor.getHighlightListView().getItems().get(1));
        assertEquals(leftEditor.getHighlightListView().getItems().get(17), rightEditor.getHighlightListView().getItems().get(17));
        assertEquals(leftEditor.getHighlightListView().getItems().get(18), rightEditor.getHighlightListView().getItems().get(18));




    }


    @Test
    public void resetTest() throws IOException {
        clickOn("#menuFile");
        clickOn("#menuReset");

        Node tPane = find("#tabPane");
        assertNotNull("tabPane is null", tPane);

        assertSnapshotsEqual(getClass().getResource("../undecoratedRootScene.png").getPath(), tPane, 1);
        assertEquals(FileManager.getFileManagerInterface().getString(FileManagerInterface.SideOfEditor.Left), "");
        assertEquals(FileManager.getFileManagerInterface().getString(FileManagerInterface.SideOfEditor.Right), "");
        controlPaneSceneInitialButtonClickTest();
    }

    @Test
    public void singleBlockMergeTest() throws IOException {
        controlPaneSceneButtonCompareClickTest();
        clickOn(".isDifferent");
        clickOn("#btnMergeRight");

        Node tPane = find("#tabPane");
        assertNotNull("tabPane is null", tPane);

        Node leftSplit = s.getScene().lookup("#leftSplit");
        Node rightSplit = s.getScene().lookup("#rightSplit");

        HighlightEditorInterface leftEditor = (HighlightEditorInterface) leftSplit.lookup("#editor");
        HighlightEditorInterface rightEditor = (HighlightEditorInterface) rightSplit.lookup("#editor");

        //then :

        // UI Testing
        assertSnapshotsEqual(getClass().getResource("../singleMergeResult.png").getPath(), tPane, 1);

        // System Testing
        assertEquals(leftEditor.getHighlightListView().getItems().get(0), rightEditor.getHighlightListView().getItems().get(0));
        assertEquals(leftEditor.getHighlightListView().getItems().get(1), rightEditor.getHighlightListView().getItems().get(1));

    }

    @After
    public void tearDown() throws TimeoutException {
        FxToolkit.cleanupStages();
        FxToolkit.hideStage();

        FileManager.getFileManagerInterface().resetModel(FileManagerInterface.SideOfEditor.Left);
        FileManager.getFileManagerInterface().resetModel(FileManagerInterface.SideOfEditor.Right);

        release(new KeyCode[] {});
        release(new MouseButton[] {});
    }

    private <T extends Node> T find(final String query) {
        return lookup(query).query();
    }

}