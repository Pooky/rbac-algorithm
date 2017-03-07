SELECT t1.id_user,t2.id_permission FROM user_role as t1
JOIN permission_role as t2 ON t1.id_role = t2.id_role

UNION 

SELECT * FROM user_permission ORDER BY id_user;