mw.loader.using('mediawiki.util',function(){if(mw.config.get('wgArticleId')===0&&mw.config.get('wgNamespaceNumber')==2){var titleParts=mw.config.get('wgPageName').split('/');if(titleParts.length==2){var userSkinPage=titleParts.shift()+'/'+mw.config.get('skin');if(titleParts.slice(-1)=='skin.js'){window.location.href=mw.util.wikiGetlink(userSkinPage+'.js');}else if(titleParts.slice(-1)=='skin.css'){window.location.href=mw.util.wikiGetlink(userSkinPage+'.css');}}}window.addPortletLink=function(){return mw.util.addPortletLink.apply(mw.util,arguments);};window.getURLParamValue=function(){return mw.util.getParamValue.apply(mw.util,arguments);};var extraCSS=mw.util.getParamValue("withCSS");if(extraCSS&&extraCSS.match(/^MediaWiki:[^&<>=%]*\.css$/)){importStylesheet(extraCSS);}var extraJS=mw.util.getParamValue("withJS");if(extraJS&&extraJS.match(/^MediaWiki:[^&<>=%]*\.js$/)){importScript(extraJS);}if(wgAction=='edit'||wgAction=='submit'||wgPageName=='Special:Upload'){importScript(
'MediaWiki:Common.js/edit.js');}else if(mw.config.get('wgPageName')=='Special:Watchlist'){importScript('MediaWiki:Common.js/watchlist.js');}if(wgNamespaceNumber==6){importScript('MediaWiki:Common.js/file.js');}mw.loader.load('//meta.wikimedia.org/w/index.php?title=MediaWiki:Wikiminiatlas.js&action=raw&ctype=text/javascript&smaxage=21600&maxage=86400');if($.client.profile().name=='msie'){importScript('MediaWiki:Common.js/IEFixes.js');if($.client.profile().versionBase=='6'){importScript('MediaWiki:Common.js/IE60Fixes.js');}}window.hasClass=(function(){var reCache={};return function(element,className){return(reCache[className]?reCache[className]:(reCache[className]=new RegExp("(?:\\s|^)"+className+"(?:\\s|$)"))).test(element.className);};})();function LinkFA(){if(document.getElementById("p-lang")){var InterwikiLinks=document.getElementById("p-lang").getElementsByTagName("li");for(var i=0;i<InterwikiLinks.length;i++){if(document.getElementById(InterwikiLinks[i].className+"-fa")){
InterwikiLinks[i].className+=" FA";InterwikiLinks[i].title="This is a featured article in another language.";}else if(document.getElementById(InterwikiLinks[i].className+"-ga")){InterwikiLinks[i].className+=" GA";InterwikiLinks[i].title="This is a good article in another language.";}}}}$(LinkFA);var autoCollapse=2;var collapseCaption="hide";var expandCaption="show";window.collapseTable=function(tableIndex){var Button=document.getElementById("collapseButton"+tableIndex);var Table=document.getElementById("collapsibleTable"+tableIndex);if(!Table||!Button){return false;}var Rows=Table.rows;if(Button.firstChild.data==collapseCaption){for(var i=1;i<Rows.length;i++){Rows[i].style.display="none";}Button.firstChild.data=expandCaption;}else{for(var i=1;i<Rows.length;i++){Rows[i].style.display=Rows[0].style.display;}Button.firstChild.data=collapseCaption;}}
function createCollapseButtons(){var tableIndex=0;var NavigationBoxes=new Object();var Tables=document.getElementsByTagName("table");for(var i=0;i<Tables.length;i++){if(hasClass(Tables[i],"collapsible")){var HeaderRow=Tables[i].getElementsByTagName("tr")[0];if(!HeaderRow)continue;var Header=HeaderRow.getElementsByTagName("th")[0];if(!Header)continue;NavigationBoxes[tableIndex]=Tables[i];Tables[i].setAttribute("id","collapsibleTable"+tableIndex);var Button=document.createElement("span");var ButtonLink=document.createElement("a");var ButtonText=document.createTextNode(collapseCaption);Button.className="collapseButton";ButtonLink.style.color=Header.style.color;ButtonLink.setAttribute("id","collapseButton"+tableIndex);ButtonLink.setAttribute("href","#");addHandler(ButtonLink,"click",new Function("evt","collapseTable("+tableIndex+" ); return killEvt( evt );"));ButtonLink.appendChild(ButtonText);Button.appendChild(document.createTextNode("["));Button.appendChild(ButtonLink);Button.
appendChild(document.createTextNode("]"));Header.insertBefore(Button,Header.firstChild);tableIndex++;}}for(var i=0;i<tableIndex;i++){if(hasClass(NavigationBoxes[i],"collapsed")||(tableIndex>=autoCollapse&&hasClass(NavigationBoxes[i],"autocollapse"))){collapseTable(i);}else if(hasClass(NavigationBoxes[i],"innercollapse")){var element=NavigationBoxes[i];while(element=element.parentNode){if(hasClass(element,"outercollapse")){collapseTable(i);break;}}}}}$(createCollapseButtons);var NavigationBarHide='['+collapseCaption+']';var NavigationBarShow='['+expandCaption+']';window.toggleNavigationBar=function(indexNavigationBar){var NavToggle=document.getElementById("NavToggle"+indexNavigationBar);var NavFrame=document.getElementById("NavFrame"+indexNavigationBar);if(!NavFrame||!NavToggle){return false;}if(NavToggle.firstChild.data==NavigationBarHide){for(var NavChild=NavFrame.firstChild;NavChild!=null;NavChild=NavChild.nextSibling){if(hasClass(NavChild,'NavContent')||hasClass(NavChild,'NavPic')){
NavChild.style.display='none';}}NavToggle.firstChild.data=NavigationBarShow;}else if(NavToggle.firstChild.data==NavigationBarShow){for(var NavChild=NavFrame.firstChild;NavChild!=null;NavChild=NavChild.nextSibling){if(hasClass(NavChild,'NavContent')||hasClass(NavChild,'NavPic')){NavChild.style.display='block';}}NavToggle.firstChild.data=NavigationBarHide;}}
function createNavigationBarToggleButton(){var indexNavigationBar=0;var divs=document.getElementsByTagName("div");for(var i=0;NavFrame=divs[i];i++){if(hasClass(NavFrame,"NavFrame")){indexNavigationBar++;var NavToggle=document.createElement("a");NavToggle.className='NavToggle';NavToggle.setAttribute('id','NavToggle'+indexNavigationBar);NavToggle.setAttribute('href','javascript:toggleNavigationBar('+indexNavigationBar+');');var isCollapsed=hasClass(NavFrame,"collapsed");for(var NavChild=NavFrame.firstChild;NavChild!=null&&!isCollapsed;NavChild=NavChild.nextSibling){if(hasClass(NavChild,'NavPic')||hasClass(NavChild,'NavContent')){if(NavChild.style.display=='none'){isCollapsed=true;}}}if(isCollapsed){for(var NavChild=NavFrame.firstChild;NavChild!=null;NavChild=NavChild.nextSibling){if(hasClass(NavChild,'NavPic')||hasClass(NavChild,'NavContent')){NavChild.style.display='none';}}}var NavToggleText=document.createTextNode(isCollapsed?NavigationBarShow:NavigationBarHide);NavToggle.appendChild(
NavToggleText);for(var j=0;j<NavFrame.childNodes.length;j++){if(hasClass(NavFrame.childNodes[j],"NavHead")){NavToggle.style.color=NavFrame.childNodes[j].style.color;NavFrame.childNodes[j].appendChild(NavToggle);}}NavFrame.setAttribute('id','NavFrame'+indexNavigationBar);}}}$(createNavigationBarToggleButton);if(wgPageName=='Main_Page'||wgPageName=='Talk:Main_Page'){$(function(){mw.util.addPortletLink('p-lang','//meta.wikimedia.org/wiki/List_of_Wikipedias','Complete list','interwiki-completelist','Complete list of Wikipedias');});}ts_alternate_row_colors=false;function uploadwizard_newusers(){if(wgNamespaceNumber==4&&wgTitle=="Upload"&&wgAction=="view"){var oldDiv=document.getElementById("autoconfirmedusers"),newDiv=document.getElementById("newusers");if(oldDiv&&newDiv){if(typeof wgUserGroups=="object"&&wgUserGroups){for(i=0;i<wgUserGroups.length;i++){if(wgUserGroups[i]=="autoconfirmed"){oldDiv.style.display="block";newDiv.style.display="none";return;}}}oldDiv.style.display="none";newDiv
.style.display="block";return;}}}$(uploadwizard_newusers);function addEditIntro(name){$('.editsection, #ca-edit').find('a').each(function(i,el){el.href=$(this).attr("href")+'&editintro='+name;});}if(wgNamespaceNumber===0){$(function(){if(document.getElementById('disambigbox')){addEditIntro('Template:Disambig_editintro');}});$(function(){var cats=document.getElementById('mw-normal-catlinks');if(!cats){return;}cats=cats.getElementsByTagName('a');for(var i=0;i<cats.length;i++){if(cats[i].title=='Category:Living people'||cats[i].title=='Category:Possibly living people'){addEditIntro('Template:BLP_editintro');break;}}});}if(mw.config.get('wgServer')=='https://secure.wikimedia.org'){importScript('MediaWiki:Common.js/secure.js');}else if(document.location&&document.location.protocol&&document.location.protocol=="https:"){importScript('MediaWiki:Common.js/secure new.js');}});;mw.loader.state({"site":"ready"});

/* cache key: enwiki:resourceloader:filter:minify-js:7:b3653ae79ccb27047df792ee7d44698c */
