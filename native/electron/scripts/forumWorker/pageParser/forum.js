"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.state = exports.newTask = exports.data = void 0;
// 解析的页面：
// forum-[a-zA-Z0-9]+-[0-9]+\.html
let forum = {
  info: document.querySelector('#ct > div > div.bm.bml.pbn > div.bm_c.cl > div:nth-child(2)').innerHTML,
  threads: []
};
let threads = {};
let subBrowseUrl = [];

for (let i of document.querySelectorAll('#threadlisttableid > tbody > tr')) {
  if (i.className !== "") continue; // 如果连 id 也没有，说明为无效节点，直接跳过

  if (!i.querySelector('th > a.s.xst')) continue;
  let id = /thread-([0-9]+)-/.exec(i.querySelector('th > a.s.xst').getAttribute('href'))[1];
  forum.threads.push(id);
  let info = {
    author: i.querySelector('td:nth-child(3) > cite > a') && /uid=([0-9]+)/.exec(i.querySelector('td:nth-child(3) > cite > a').getAttribute('href'))[1] || "0",
    title: i.querySelector('th > a.s.xst').innerText.trim(),
    type: i.querySelector('th > em > a') && /typeid=([0-9]+)/.exec(i.querySelector('th > em > a').getAttribute('href'))[1] || "0",
    states: {}
  };
  subBrowseUrl.push(info.author); // 帖子状态解析

  let state = i.querySelector('td.icn > a').getAttribute('title');
  exports.state = state;
  if (/关闭的主题/.test(state)) info.states.closed = true;
  if (/全局置顶主题/.test(state)) info.states.topLevel = 3;else if (/分类置顶主题/.test(state)) info.states.topLevel = 2;else if (/本版置顶主题/.test(state)) info.states.topLevel = 1; // 标题状态解析

  if (i.querySelector('th > a.s.xst').style.color) info.states.titleColor = i.querySelector('th > a.s.xst').style.color.trim();
  if (i.querySelector('th > a.s.xst').style.fontWeight === 'bold') info.states.titleBold = true; // 帖子分级解析

  for (let j of i.querySelectorAll('th > img')) {
    switch (j.getAttribute('src')) {
      case 'template/mcbbs/image/digest_1.gif':
        // 精华 I
        info.states.starLevel = 1;
        break;

      case 'template/mcbbs/image/digest_2.gif':
        // 精华 II
        info.states.starLevel = 2;
        break;

      case 'template/mcbbs/image/digest_3.gif':
        // 精华 III
        info.states.starLevel = 3;
        break;
    } // 图章检测


    if (/static\/image\/stamp\/[0-9]+\.small\.gif/.test(j.getAttribute('src'))) {
      info.stamp = j.getAttribute('alt');
    }
  } // 帖子统计解析


  info.replyCount = i.querySelector('td.num > a').innerText.trim();
  info.watchCount = i.querySelector('td.num > em').innerText.trim();
  threads[id] = info;
}

let exportData = {
  forum: forum
};
let data = exportData;
exports.data = data;
let newTask = subBrowseUrl;
exports.newTask = newTask;
let state = 'newTask';
exports.state = state;