<%--
  Created by IntelliJ IDEA.
  User: Tzh
  Date: 2019/4/22
  Time: 13:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>实时姿态Unity3D呈现</title>
    <link rel="shortcut icon" href="/PoseEstimation/webGL/TemplateData/favicon.ico">
    <link rel="stylesheet" href="/PoseEstimation/webGL/TemplateData/style.css">
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script><!-- 引入vue框架 -->
    <script src="https://unpkg.com/element-ui/lib/index.js"></script><!-- 引入element组件库 -->
    <script src="/PoseEstimation/js/jquery-3.3.1.min.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css"><!-- 引入element样式 -->
    <script src="/PoseEstimation/webGL/TemplateData/UnityProgress.js"></script>
    <script src="/PoseEstimation/webGL/Build/UnityLoader.js"></script>
</head>
<style>
    .el-card{
        border: none;
    }
</style>
<body>

<el-container id="webGL">
    <el-main>
        <el-row style="height: 100%">
            <el-col :span="16" style="height: 100%">
                <el-card class="box-card" :body-style="{ padding:'0px'}">
                    <div slot="header" class="clearfix">
                        <span>实时Unity3D画面</span>
                    </div>
                    <div>
                        <div id="gameContainer" style="width: 100%; height: 100%"></div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="8" style="height: 100%">
                <el-card class="box-card" style="height: 60%">
                    <div slot="header" class="clearfix">
                        <span>设置面板</span>
                        <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>
                    </div>
                    <el-form label-position="left" label-width="200px">
                        <el-form-item label="切换人物模型">
                            <el-select v-model="chosenCharacter" placeholder="选择角色">
                                <el-option v-for="option in characterOptions" :key="option.value" :label="option.label" :value="option.value">
                                </el-option>
                            </el-select><span>{{chosenCharacter}}</span>
                        </el-form-item>
                        <el-form-item label="切换场景">
                            <el-select v-model="chosenScene" placeholder="选择场景">
                                <el-option v-for="option in sceneOptions" :key="option.value" :label="option.label" :value="option.value">
                                </el-option>
                            </el-select><span>{{chosenScene}}</span>
                        </el-form-item>
                        <el-button :type="buttonType" @click="change"><span v-if="enable">禁用脚本</span><span v-if="!enable">激活脚本</span></el-button>
                        <el-button type="success" @click="sendWS">发送WS测试</el-button>
                    </el-form>
                </el-card>
                <el-card class="box-card" style="height: 40%" :body-style="{ padding:'0px'}">
                    <div slot="header" class="clearfix">
                        <span>实时摄像头采集画面</span>
                        <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>
                    </div>
                    <div style="width: 100%; height: 100%; color: black"></div>
                </el-card>
            </el-col>

        </el-row>

        <%--<el-button :disabled="!enable" type="primary" @click='handleClick'>模拟后端发送数据</el-button></br>--%>

    </el-main>
</el-container>



<script>
    var webGL = new Vue({
        el:'#webGL',
        data: {
            enable:true,
            buttonType:'danger',
            gameInstance:'',
            characterOptions:[{
                label:'男性角色',
                value:'男性角色'
            },{
                label:'女性角色',
                value:'女性角色'
            }],
            sceneOptions:[{
                label:'户外场景',
                value:'户外场景'
            },{
                label:'室内场景',
                value:'室内场景'
            }],
            modeOptions:[{
                label:'动画舞蹈',
                value:'dance'
            },{
                label:'动画舞蹈',
                value:'dance'
            }],
            chosenCharacter:'',
            chosenScene:''
        },
        mounted: function(){
            // this.initWebSocket();
            this.gameInstance = UnityLoader.instantiate("gameContainer", "/PoseEstimation/webGL/Build/PoseTest.json", {onProgress: UnityProgress});
        },
        methods: {
            sendWS: function(){
                $.ajax({
                    url: "/PoseEstimation/sendWS",
                    type: "post",
                    // data: {"msg": `[[-310.0, 0.0, 623.5089664258754], [-424.1574093680099, 15.583263081533788, 666.5416614859057], [-540.0269456741221, 164.5036349256959, 207.2636815507969], [-638.6584253091446, 363.01971400693253, -259.1012302985142], [-195.84379669967166, -15.58294877185412, 580.4767907207972], [-303.31246202770967, 180.0059083524335, 117.57152580064178], [-78, -120.13229995574936, 1059.8878405442852], [-44.900039569158196, -213.7205260075329, 1068.6537824383108], [-7.29497447728869, -214.45539235093423, 1152.752063380103], [-93.74134466442965, -156.3460726129531, 1029.0660785226953], [-903.3416210827296, -282.34365134178046, 1251.1437435861014], [-1058.6735471008112, -149.378470352472, 1337.1718156419754], [-207.668802285023, -85.39012810748576, 1044.6012162088678], [-413.59457402841883, 23.841331554802075, 877.6912087343269], [-262.9590659911815, -124.82383592106339, 742.3612180702864]]`},
                    success: function () {
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        console.log(textStatus);
                    }
                });
            },
            change:function(){
                this.gameInstance.SendMessage("Philip", "ChangeState"); //控制WebGL激活/禁用脚本
                this.enable = !this.enable;
                if(this.enable){
                    this.buttonType = 'danger';

                }else{
                    this.buttonType = 'success';
                }
            },
            // handleClick: function() {
            //     this.socket.send("Hello!");
            // },
            initWebSocket: function () {
                if(window.WebSocket){
                    console.log("初始化WebSocket！");
                    this.socket = new WebSocket('ws://localhost:8080/PoseEstimation/webSocket'); //TODO
                    this.socket.onopen = this.onOpen;
                    this.socket.onmessage = this.onMessage;
                    this.socket.onerror = this.onError;
                    this.socket.onclose = this.onClose;
                }else {
                    console.log("不支持WebSocket！");
                }
            },
            onOpen: function () {
                console.log("WebSocket连接成功!");
                this.socket.send("Hello!");
            },
            // onMessage: function(event){
            //     if(event.data){
            //         console.log("接收到来自后台的消息：" + event.data);
            //         this.gameInstance.SendMessage("Philip", "GetPose", event.data); //调用Unity内部方法，将姿态数据传入
            //     }
            // },
            onError: function(){
                console.log("WebSocket连接发生错误！");
            },
            onClose: function(){
                console.log("WebSocket关闭！");
            }
        }
    })
</script>
</body>
</html>

