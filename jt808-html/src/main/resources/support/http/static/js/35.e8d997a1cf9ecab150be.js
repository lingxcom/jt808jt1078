webpackJsonp([35],{AeFI:function(e,t){},S0xT:function(e,t,i){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var l={name:"Log",data:function(){var e=new Date;e.setHours(0),e.setMinutes(0),e.setSeconds(0);var t=new Date;return{tids:[],gids:[],form:{time:[e.getTime(),t.getTime()],only:!0,type:["soc","mcu","dmesg","qxdm","tcpdump","coredump"],ip:"61.144.49.140",port:"21",account:"",password:"",path:"/mnt/sdcard/app/rtk"}}},methods:{fun0xF006:function(){0!=this.tids.length||0!=this.gids.length?this.callApi({apicode:3402,tids:this.tids,gids:this.gids},function(e){1==e.code?this.lgxInfo("操作成功，日志查询指令已下发!"):this.lgxInfo2("日志查询指令下发失败!")}):this.lgxInfo2("操作失败，请选择要操作的车辆!")},fun0xF007:function(){if(0!=this.tids.length||0!=this.gids.length){var e=this.form;e.apicode=3403,e.tids=this.tids,e.gids=this.gids,e.stime=this.form.time[0],e.etime=this.form.time[1],this.callApi(e,function(e){1==e.code?this.lgxInfo("操作成功，日志请求指令已下发!"):this.lgxInfo2("日志请求指令下发失败!")})}else this.lgxInfo2("操作失败，请选择要操作的车辆!")},lingxcheckchange:function(e,t){if(t)(e.id+"").indexOf("_")>0&&this.tids.push(e.value+""),this.gids.push(e.value);else{for(var i=-1,l=0;l<this.tids.length;l++)if(e.value==this.tids[l]){i=l;break}i>=0&&this.tids.splice(i,1),i=-1;for(l=0;l<this.gids.length;l++)if(e.value==this.gids[l]){i=l;break}i>=0&&this.gids.splice(i,1)}}},mounted:function(){},components:{LingxCarTree:i("Zwc7").a}},a={render:function(){var e=this,t=e.$createElement,i=e._self._c||t;return i("div",{staticStyle:{height:"100%"}},[i("el-container",{staticStyle:{height:"100%"}},[i("el-aside",{staticStyle:{"background-color":"#fff","border-right":"#c1c5cd 1px solid"},attrs:{width:"300px"}},[i("LingxCarTree",{ref:"lingxCarTree",on:{lingxcheckchange:e.lingxcheckchange}})],1),e._v(" "),i("el-container",{staticStyle:{width:"100%",height:"100%",margin:"0px",padding:"0px"}},[i("el-header",{staticStyle:{padding:"0px"},attrs:{height:"42"}},[i("div",{staticStyle:{margin:"5px"}},[e._v(" \n          ")])]),e._v(" "),i("el-main",{staticStyle:{margin:"0px",padding:"0px"}},[i("el-form",{ref:"form",attrs:{model:e.form,"label-width":"180px",size:"small"}},[i("el-form-item",{attrs:{label:"选择时间"}},[i("el-date-picker",{attrs:{"value-format":"timestamp",type:"datetimerange","range-separator":"至","start-placeholder":"开始日期","end-placeholder":"结束日期"},model:{value:e.form.time,callback:function(t){e.$set(e.form,"time",t)},expression:"form.time"}})],1),e._v(" "),i("el-form-item",{attrs:{label:"日志类型"}},[i("el-checkbox-group",{model:{value:e.form.type,callback:function(t){e.$set(e.form,"type",t)},expression:"form.type"}},[i("el-checkbox",{attrs:{label:"soc",name:"type"}}),e._v(" "),i("el-checkbox",{attrs:{label:"mcu",name:"type"}}),e._v(" "),i("el-checkbox",{attrs:{label:"dmesg",name:"type"}}),e._v(" "),i("el-checkbox",{attrs:{label:"qxdm",name:"type"}}),e._v(" "),i("el-checkbox",{attrs:{label:"tcpdump",name:"type"}}),e._v(" "),i("el-checkbox",{attrs:{label:"coredump",name:"type"}})],1)],1),e._v(" "),i("el-form-item",{attrs:{label:"只上传文件列表"}},[i("el-switch",{model:{value:e.form.only,callback:function(t){e.$set(e.form,"only",t)},expression:"form.only"}})],1),e._v(" "),i("el-form-item",{attrs:{label:"日志域名"}},[i("el-input",{model:{value:e.form.ip,callback:function(t){e.$set(e.form,"ip",t)},expression:"form.ip"}})],1),e._v(" "),i("el-form-item",{attrs:{label:"日志端口"}},[i("el-input",{model:{value:e.form.port,callback:function(t){e.$set(e.form,"port",t)},expression:"form.port"}})],1),e._v(" "),i("el-form-item",{attrs:{label:"日志账号"}},[i("el-input",{model:{value:e.form.account,callback:function(t){e.$set(e.form,"account",t)},expression:"form.account"}})],1),e._v(" "),i("el-form-item",{attrs:{label:"日志密码"}},[i("el-input",{model:{value:e.form.password,callback:function(t){e.$set(e.form,"password",t)},expression:"form.password"}})],1),e._v(" "),i("el-form-item",{attrs:{label:"日志路径"}},[i("el-input",{model:{value:e.form.path,callback:function(t){e.$set(e.form,"path",t)},expression:"form.path"}})],1),e._v(" "),i("el-form-item",[i("el-button",{attrs:{type:"primary"},on:{click:function(t){return e.fun0xF007()}}},[e._v("日志请求0xF007")])],1)],1)],1)],1)],1)],1)},staticRenderFns:[]};var s=i("VU/8")(l,a,!1,function(e){i("AeFI")},null,null);t.default=s.exports}});