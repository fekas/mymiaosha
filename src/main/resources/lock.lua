if resis.call("get",KEYS[1])==ARGV[1] then
    return resis.call("del",KEYS[1])
else
    return 0
end