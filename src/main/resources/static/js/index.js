$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//获取标题和内容
	var title=$("#recipient-name").val();
	var content=$("#message-text").val();


	//发布异步请求
	$.post(
		"/community/discuss/add",
		{"title":title,"content":content},
		function(data){
			//回调函数
			data=$.parseJSON(data);
			//在提示框中显示返回信息
			$("#hintModal").modal("show");
			//2秒后自动隐藏
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//刷新页面
				if(data.code==0){
					window.location.reload();
				}
			}, 2000);
		}
	);

}