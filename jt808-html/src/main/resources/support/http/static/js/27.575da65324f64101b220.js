webpackJsonp([27],{"HGO/":function(t,i,n){"use strict";Object.defineProperty(i,"__esModule",{value:!0});var e={render:function(){var t=this,i=t.$createElement,n=t._self._c||i;return n("div",{staticStyle:{height:"100%"}},[n("el-container",{staticStyle:{height:"100%"}},[n("el-aside",{staticStyle:{padding:"10px"},attrs:{width:"300px"}},[n("el-input",{attrs:{placeholder:"扫码输入控件,焦点不可移开"},nativeOn:{keyup:function(i){return i.type.indexOf("key")||13===i.keyCode?t.inputNo.apply(null,arguments):null}},model:{value:t.input,callback:function(i){t.input=i},expression:"input"}}),t._v(" "),n("ul",t._l(t.history,function(i,e){return n("li",{staticStyle:{"list-style-type":"demical","line-height":"26px"}},[e%2==1?n("div",{staticStyle:{"background-color":"#f1f1f1"}},[t._v(" "+t._s(i))]):t._e(),t._v(" "),e%2==0?n("div",[t._v(" "+t._s(i))]):t._e()])}),0)],1),t._v(" "),n("el-main",{staticStyle:{"background-color":"#e2ece3"}},[n("b",[t._v("当前装箱箱号："),n("span",{staticStyle:{color:"red"}},[t._v(t._s(t.currentNo))])]),t._v(" "),n("br"),t._v(" "),n("span",{staticStyle:{color:"#999","font-size":"12px"}},[t._v("注意：在用扫描枪时，电脑输入法要切换为英文输入否则有可能扫码不正常")]),t._v(" "),n("br"),t._v(" "),n("br"),t._v(" "),n("el-row",{attrs:{gutter:20}},t._l(t.map,function(i,e,o){return n("el-col",{key:o,attrs:{span:6}},[n("div",{staticStyle:{"background-color":"#FFF",color:"#69763d",border:"1px solid #69763d","border-radius":"10px",padding:"20px","margin-bottom":"20px"}},[t._v("\n        箱号："),n("b",[t._v(t._s(e))]),t._v(" "),n("ul",{staticStyle:{padding:"0px"}},t._l(i,function(e,o,r){return n("li",{key:r,staticStyle:{"list-style-type":"none"}},[t._v("\n            "+t._s(o)+"  【"+t._s(i[o])+"】\n")])}),0)])])}),1)],1)],1),t._v(" "),n("audio",{ref:"audio"},[n("source",{attrs:{type:"audio/mpeg"}})])],1)},staticRenderFns:[]};var o=n("VU/8")({name:"Packing",data:function(){return{test:"",input:"",currentNo:"必须先扫描箱号",history:[],map:{},mp3Src1:"/static/mp3/1.mp3",mp3Src2:"/static/mp3/2.mp3"}},methods:{one:function(){this.$refs.audio.src=this.mp3Src1,this.$refs.audio.play()},two:function(){this.$refs.audio.src=this.mp3Src2,this.$refs.audio.play()},add:function(t,i,n){var e=this.map[t][i]+1;this.$set(this.map[t],i,e),this.callApi({apicode:1002,no:t,fnsku:i},function(t){})},del:function(t,i,n){var e=this.map[t][i]-1;this.$set(this.map[t],i,e),0===this.map[t][i]&&delete this.map[t][i],this.callApi({apicode:1002,no:t,fnsku:i,operat:"-"},function(t){})},inputNo:function(){var t=this;this.input&&this.callApi({apicode:1001,code:this.input},function(i){if(1==i.data)t.one(),t.lgxInfo(t.input+" => 箱号"),t.currentNo=t.input,t.history.unshift(t.input),t.callApi({apicode:1003,no:t.currentNo},function(i){t.$set(t.map,t.currentNo,i.data)});else if(2==i.data){var n=i.sku;if(!t.currentNo||"必须先扫描箱号"==t.currentNo)return t.two(),t.lgxInfo2("操作失败，请先输入装箱箱号"),void(t.input="");t.one(),t.lgxInfo(t.input+" => 商品FNSKU"),t.map[t.currentNo]||(t.map[t.currentNo]={});var e=t.map[t.currentNo];e[n]||(e[n]=0),e[n]++,t.callApi({apicode:1002,no:t.currentNo,fnsku:t.input},function(t){}),t.history.unshift(t.input)}else t.two(),t.lgxInfo2("无效编码，请确认是否已导入:"+t.input);t.input=""})}}},e,!1,function(t){n("aEPK")},null,null);i.default=o.exports},aEPK:function(t,i){}});