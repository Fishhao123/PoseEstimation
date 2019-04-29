<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Unity WebGL Player | test</title>
    <link rel="shortcut icon" href="/webGL/TemplateData/favicon.ico">
    <link rel="stylesheet" href="/webGL/TemplateData/style.css">
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script><!-- 引入vue框架 -->
    <script src="https://unpkg.com/element-ui/lib/index.js"></script><!-- 引入element组件库 -->
    <link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css"><!-- 引入element样式 -->
    <script src="/webGL/TemplateData/UnityProgress.js"></script>
    <script src="/webGL/Build/UnityLoader.js"></script>
</head>
<body>

<el-container id="webGL">
    <el-main>
        <el-row >
            <el-col :span="16" >

                    <el-card class="box-card" :body-style="{ padding:'0px'}">
                        <div slot="header" class="clearfix">
                            <span>实时Unity3D画面</span>
                        </div>
                        <div>
                            <div id="gameContainer" style="width: 100%; height: 100%"></div>
                        </div>
                    </el-card>
            </el-col>
            <el-col :span="8">
                <el-card class="box-card" style="height: 20%">
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
                    </el-form>
                </el-card>
                <el-card class="box-card" style="height: 60%">
                <div slot="header" class="clearfix">
                    <span>实时摄像头采集画面</span>
                    <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>
                </div>

            </el-card>
            </el-col>

        </el-row>

        <%--<el-button :disabled="!enable" type="primary" @click='handleClick'>模拟后端发送数据</el-button></br>--%>
        <%--<el-button :type="buttonType" @click="change"><span v-if="enable">禁用脚本</span><span v-if="!enable">激活脚本</span></el-button>--%>
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
            chosenCharacter:'',
            chosenScene:''
        },
        mounted: function(){
            // this.initWebSocket();
            this.gameInstance = UnityLoader.instantiate("gameContainer", "/webGL/Build/test.json", {onProgress: UnityProgress});
        },
        methods: {
            change:function(){
                this.gameInstance.SendMessage("Ethan", "ChangeState"); //控制WebGL激活/禁用脚本
                this.enable = !this.enable;
                if(this.enable){
                    this.buttonType = 'danger';

                }else{
                    this.buttonType = 'success';
                }
            },
            handleClick: function() {
                this.socket.send("Hello!");
            },
            initWebSocket: function () {
                if(window.WebSocket){
                    console.log("初始化WebSocket！");
                    this.socket = new WebSocket('ws://localhost:8080/Party/webSocket');
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
            },
            onMessage: function(event){
                if(event.data){
                    console.log("接收到来自后台的消息：" + event.data);
                    this.gameInstance.SendMessage("Ethan", "GetPose", event.data); //调用Unity内部方法，将姿态数据传入
                }
            },
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

