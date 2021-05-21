/**
 * @license Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
    /**
     * 설정법
     * plugins > ckeditor > samples > index.html 실행
     */
    // sample 용
    // config.language = 'es';
    // config.uiColor = '#F7B42C';
    // config.height = 300;
    // config.toolbarCanCollapse = true;



    // file setting
    config.language = 'ko';
    config.extraPlugins = 'uploadimage,filemanager,notification,clipboard,textmatch,autolink';
    config.filebrowserBrowseUrl = CONTEXT_PATH + '/ckeditor/fileBrowser?targetType=CKEDITOR&targetId=' + (UUID || "");
    config.filebrowserWindowWidth = '960';
    config.filebrowserWindowHeight = '600';
    config.imageUploadUrl = CONTEXT_PATH + '/ckeditor/uploadImage?&targetId=' + (UUID || "");

    config.removeDialogTabs = 'link:target;link:advanced;image:Link;image:advanced';
    config.font_names = '맑은 고딕/Malgun Gothic;굴림/Gulim;돋움/Dotum;바탕/Batang;궁서/Gungsuh;' + config.font_names;
    config.toolbarCanCollapse = true;
    config.toolbarStartupExpanded = false;
    config.removePlugins = 'elementspath';
    config.resize_enabled = false;

    // 지울 버튼 목록
    // 주석 내용 : 그룹명 > 상세명
    config.removeButtons =
        // document > document
        'Save,NewPage,' +
        // document > doctools
        'Templates,' +

        // clipboard > clipboard
        'Cut,Copy,Paste,PasteText,PasteFromWord,' +
        // clipboard > undo
        'Undo,Redo,' +

        // editing > spellchecker
        'Scayt,' +
        // editing > find
        'Find,Replace,' +
        // editing > selecttion
        'SelectAll,' +

        // form > form
        'Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,' +

        // insert > insert
        'Image,Flash,SpecialChar,PageBreak,Iframe,' +

        // basicstyles > basicstyles
        'Subscript,Superscript,' +
        // basicstyles > cleanup
        'CopyFormatting,RemoveFormat,' +

        // paragraph > blocks
        'Blockquote,CreateDiv,' +
        // paragraph > bidi
        'BidiLtr,BidiRtl,Language,' +

        // tools > tools
        'Maximize,ShowBlocks,' +

        //aboout > about
        'About';

    config.toolbarGroups = [
        { name: 'others', groups: [ 'others' ] },
        { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
        { name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
        { name: 'forms', groups: [ 'forms' ] },
        { name: 'insert', groups: [ 'insert' ] },
        { name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
        '/',
        { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
        { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
        { name: 'links', groups: [ 'links' ] },
        '/',
        { name: 'styles', groups: [ 'styles' ] },
        { name: 'colors', groups: [ 'colors' ] },
        { name: 'tools', groups: [ 'tools' ] },
        { name: 'about', groups: [ 'about' ] }
    ]
};
