function removeStyleMarks(content) {
    if (null == content)
        return "";
    let str = content;
    str = str.replace(/<xml>[\s\S]*?<\/xml>/ig, '');
    str = str.replace(/<style>[\s\S]*?<\/style>/ig, '');
    str = str.replace(/<\/?[^>]*>/g, '');
    str = str.replace(/[ | ]*\n/g, '\n');
    str = str.replace(/&nbsp;/ig, '');
    //alert(str);
    return str;
}

const MIN_ITEMCONTENT_LEN = 10;
