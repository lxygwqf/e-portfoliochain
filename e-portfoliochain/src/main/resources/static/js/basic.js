$('#myTab a:first').tab('show')
$("#xclDescTab a:first").tab('show')
//$("#mycenterTab a:first").tab('show')
$("#myDaTab a:first").tab('show')
$("#daslForm").submit(function(e){
  $(this).css('display','none')
  $("#daslFormResult").css({display:'block'})
  return false
})
$("#closedasl").onclick(function(e){
  $(this).css('display','none')
  $("#daslForm").css({display:'block'})
  return false
})