package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.expressions.ConditionBuilder;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Assistant for tests
 */
class TestHelper {

    static final String CONDITION_STRING1 = "root.$id=='123'";
    static final String CONDITION_STRING2 = "root.name$like'product%'&&-(-123--7+8)*10+4/3/(5/6)>7/2-root.creatorCode&&root.code!=null&&--123==123&&root.creatorCode>9223372036854775808";
    static final String CONDITION_STRING3 = "root.name=='product1'&&root{type=Service}.managerPersonalCode$between(1,3)";
    static final String CONDITION_STRING4 = "root.name=='product1'&&root.$type$like'Product%'";
    static final String CONDITION_STRING5 = "root{type=ProductLimited}.name=='product1'&&!'alias1'$inroot.aliases{cond=it$in['alias2','alias3']}&&root.relatedProduct.$type=='ProductLimited'&&root.relatedProduct{type=ProductLimited}.$type$like'ProductL%'&&root.relatedProduct.$id$in['10','13']&&root.relatedProduct{type=ProductLimited}.limitedOffer$like'limitedOffer%'&&'alias1'$inroot.aliases{cond=it!='alias2'}&&root.relatedProduct.aliases{cond=it$like'alias1%'}.$exists&&root.relatedProduct.relatedProduct.creatorCode==13&&root.relatedProduct.relatedProduct.aliases{cond=it=='alias41'}.$exists&&root.services{cond=it.managerPersonalCode==2}.$exists&&root.relatedProduct.services.$exists&&2$inroot.services.managerPersonalCode&&'action2'$inroot.services{cond=it{type=Service}.startAction.code$in['action2']}.startAction.code&&root.services.startAction{type=Action}.algorithmCode.$max==100&&101==root.services.startAction.algorithmCode.$sum&&'Action'$inroot.services.startAction.$type&&'14'$inroot.services.startAction.$id&&root.services{cond='1'$init{type=Service}.operations{type=OperationLimited}.limitedOffer}.$exists&&-('creatorCode'*2)+root.relatedProduct.creatorCode/3>=0&&root.code.$upper=='ProductLimited1'.$upper&&root.code.$lower=='ProductLimited1'.$lower&&root.request.$id=='15'&&'16'$inroot.services.request.$id";
    static final String CONDITION_STRING6 = "!('t'' ''1'>'t2'&&123<=124||123.456<-123.457&&D2020-03-05T10:12:34.567>=D2020-03-05T10:12:34.577)&&(true==false||true!=false)&&'t1'==null&&D2020-03-26T10:12:34Z>D2020-03-26T10:12:34.123456-08:00&&D2020-03-26T10:12:34.123456789Z>D2020-03-26T10:12:34.123+01:00&&D2020-03-26T10:12:34.123Z>D2020-03-26T10:12:34.123456Z&&D2020-03-26T10:12:34.123456789+01:00>D2020-03-26T10:12:34+02:00&&D2020-12-23T15:37:10.123+1!=null&&D2021-01-13T14:41:10.$addSeconds(1)!=null&&D2021-01-13T14:41:10+08:00.$addSeconds(1)!=null";
    static final String CONDITION_STRING7 = "root.services.$id.$min==1&&root.services.$id.$max==1&&root.services.$id.$sum==1&&root.services.$id.$avg==1&&root.services.$id.$count==1&&root.services.$count==1&&root.relatedProduct!=null&&root.relatedProduct.relatedProduct==null&&root.relatedProduct==root.relatedProduct&&root.relatedProduct!=root.relatedProduct.relatedProduct&&root.services.$exists&&root.relatedProduct$in[root.relatedProduct,root.relatedProduct.relatedProduct]&&!root.relatedProduct$inroot.services";
    static final String CONDITION_STRING8 = "root.request.initiator.firstName$like'Iv%'&&root.request.initiator.lastName$like'Iv%'&&'Ivan'$inroot.services.initiator.firstName&&root.services{cond=it.initiator.firstName$like'Iv%'}.$exists&&root.request.initiator==null&&root.request.initiator!=null";
    static final String CONDITION_STRING9 = "it$like'alias%'";
    static final String CONDITION_STRING10 = "it.code$like'service%'";
    static final String CONDITION_STRING11 = "root.p1.$length>3&&root.code.$trim=='Bye'&&root.code.$ltrim=='Bye  '&&root.code.$rtrim=='  Bye'&&root.p1.$substr(root.p2,root.p3)==root.p1.$substr(root.p4)&&root.p1.$replace(root.code,'Hey')$like'HeyHey%'&&root.p6.$round>0&&root.p6.$ceil>0&&root.p6.$floor>0&&root.p1.$hash>1&&(root.p2/2).$asString==(root.p1.$asBigDecimal*2).$asString&&root.p2.$abs==4";
    static final String CONDITION_STRING12 = "root.p7.$addMilliseconds(root.p2)==root.p14&&root.p7.$addSeconds(root.p2)==root.p14&&root.p7.$addMinutes(root.p2)==root.p14&&root.p7.$addHours(root.p2)==root.p14&&root.p7.$addDays(root.p2)==root.p14&&root.p7.$addMonths(root.p2)==root.p14&&root.p7.$addYears(root.p2)==root.p14&&root.p7.$subMilliseconds(root.p2).$subSeconds(root.p2).$subMinutes(root.p2).$subHours(root.p2).$subDays(root.p2).$subMonths(root.p2).$subYears(root.p2)==root.p14";
    static final String CONDITION_STRING13 = "root.actions{type=ActionSpecial,elemAlias=action,cond=it.parameters{type=ActionParameter,elemAlias=parameter,cond=it.parameters{cond=it.code==@action.specialOffer+@parameter{type=ActionParameterSpecial}.specialOffer}.$exists}.$exists}.$exists";
    static final String CONDITION_STRING14 = "it.parameters{type=ActionParameter,elemAlias=parameter,cond=it.parameters{cond=it.code==@action.specialOffer+@parameter{type=ActionParameterSpecial}.specialOffer}.$exists}.$exists";
    static final String CONDITION_STRING15 = "root.actions{elemAlias=action,cond=it.parameters{cond=it.code==@action.code}.$exists}.$exists&&root.actions{elemAlias=action,cond=it.parameters{cond=it.code!=@action.code}.$exists}.$exists&&root.service{alias=service}.startAction{alias=action}.parameters{cond=it.code$like@service.code+@action.code+'%'}.$count==3";
    static final String CONDITION_STRING16 = "coalesce(root.code,root.p1,root.p11)=='testSTR'";
    static final String CONDITION_STRING17 = "root.request.initiator.document.code=='document1'&&root.services.initiator.document.code.$exists&&root.request.initiator.document{type=Agreement,alias=document}.document.product.services{cond=it.code==@document.code}.$exists&&root.services.initiator.document{type=Permission}.number.$exists&&root.relatedProduct.$exists";
    static final String CONDITION_STRING18 = "root.code$inentities{type=Product,elemAlias=product,cond=it.services{cond=it.code==@product.code}.$exists}.code";
    static final String CONDITION_STRING19 = "7.5%2==1.5";
    static final String CONDITION_STRING20 = "root.code.$min+root.code.$max+root.code.$sum+root.code.$avg+root.code.$count==1";
    static final String CONDITION_STRING21 = "root.services.$map(it.operations.$count).$sum==1";
    static final String CONDITION_STRING22 = "root.aliases.$map('alias: '+it).$sum==1";
    static final String CONDITION_STRING23 = "entities{type=Test2Entity,params={character='A',string='Test',byte=127,short=32767,integer=2147483647,long=9223372036854775807,float=123.4567,double=1234567.890123456,bigDecimal=92233720368547758079223372036854775807.92233720368547758079223372036854775807,date=D2020-03-05,datetime=D2020-03-05T10:12:34.567,offsetDatetime=D2020-03-05T10:12:34.567+06:00,boolean=true,strings=['Test1','Test2','Test3']}}.$exists";
    static final String CONDITION_STRING24 = "entities{type=Test2Entity,params={character=null,string=null,byte=null,short=null,integer=null,long=null,float=null,double=null,bigDecimal=null,date=null,datetime=null,offsetDatetime=null,boolean=null,strings=[null]}}.$exists";
    static final String CONDITION_STRING25 = "now!=null";
    static final String CONDITION_STRING26 = "now.$year!=null&&now.$month!=null&&now.$day!=null&&now.$hour!=null&&now.$minute!=null&&now.$second!=null&&now.$offsetHour!=null&&now.$offsetMinute!=null&&'2023-08-21'.$asDate!=null&&'2023-08-21T14:24:10.123456'.$asDateTime!=null&&'2023-08-21T14:24:10.123456+08:00'.$asOffsetDateTime!=null";
    static final String CONDITION_STRING27 = "now.$date!=null&&now.$time!=null&&T12:46:00.$asString!=null&&'12:46'.$asTime!=null&&now.$offset!=null";
    static final String CONDITION_STRING28 = "(1&2|~3^4)!=null&&'123'.$lpad(8,'0').$rpad(10,'0')!=null&&!!1==1&&(1&1)!=null&&(1&1)==1&&(1&1)$between(1,2)&&(1&1)$in[1,2]&&1>>1==0&&1<<1==2";
    static final String CONDITION_STRING29 = "'1'==any(root.aliases)&&'1'!=any(['1','2','3'])&&'1'>all(root.aliases)&&'1'<=all(['1','2','3'])&&1<any([1])&&D2024-02-12>=all([D2024-02-12,D2024-02-13])";
    static final String CONDITION_STRING30 = "2.$power(3)!=8.$log(2)";
    static final String CONDITION_STRING31 = "root.p15.$dateTime==D2020-10-05T13:11:00.12345";

    private TestHelper() {
    }

    /**
     * Check the condition builder
     *
     * @param expressionsProcessor The expression processor
     * @param expectedString       Expected string
     * @param conditionBuilder     Condition builder
     */
    static void assertConditionBuilder(ExpressionsProcessor expressionsProcessor, String expectedString, ConditionBuilder conditionBuilder) {
        assertEquals(expectedString, conditionBuilder.build(expressionsProcessor).toString());
    }
}
